package org.example.client.calculator;

import org.example.client.computation.Computation;
import org.example.client.socket.*;
import org.example.main.completable.dto.FunctionInput;
import org.example.main.completable.dto.Signal;
import org.example.main.completable.dto.Status;

import java.util.Optional;
import java.util.concurrent.*;

public class LongTermCalculator implements CommonCalculator{
    private final LongTermClientSocketIO clientSocketIO;
    private final Computation computation;
    private ExecutorService pool;
    private final String name;
    public LongTermCalculator(LongTermClientSocketIO clientSocketIO, Computation computation, String name) {
        this.clientSocketIO = clientSocketIO;
        this.computation = computation;
        this.name = name;
        initPool();
    }

    public void calculate() {
        while (!Thread.interrupted()) {
            FunctionInput input = clientSocketIO.receiveData();
            int x = input.value();
            System.out.println(name + " received " + x);
            int signal = input.signal();
            if (signal == Signal.RESTART) {
                interruptAll();
            }
            if(signal == Signal.SHUTDOWN) {
                stopExecution();
                break;
            }
            executeAsync(input);
        }
    }
    private void initPool() {
        pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()-1);
    }
    private void interruptAll() {
        pool.shutdownNow();
        initPool();
    }
    private void stopExecution() {
        pool.shutdownNow();
        pool.close();
    }

    private void executeAsync(FunctionInput input) {
        pool.submit(() -> executeComputation(input));
    }

    private void executeComputation(FunctionInput input) {
        int x = input.value();
        long timeout = input.timeout();
        Executor executor = new Executor(computation);
        try {
            computeFunctionAt(x, timeout, executor);
        } catch (ExecutionException e) {
            clientSocketIO.sendData(name, x, Status.FATAL_ERROR, 0,
                    "Calculation finished with execution error");
        } catch (TimeoutException e) {
            clientSocketIO.sendData(name, x, Status.TIMEOUT, 0,
                    "Execution timeout. Total light errors: " + executor.getLightErrors());
        } catch (InterruptedException e) {
            clientSocketIO.sendData(name, x, Status.INTERRUPT, 0, "Calculation was interrupted");
            Thread.currentThread().interrupt();
        }
    }

    private void computeFunctionAt(int x, long timeout, Executor executor) throws InterruptedException, ExecutionException, TimeoutException {
        Optional<Optional<Integer>> result = executor.execute(x).get(timeout, TimeUnit.MILLISECONDS);
        System.out.println("Result " + result);
        if (result.isEmpty()) {
            clientSocketIO.sendData(name, x, Status.LIGHT_ERROR_LIMIT, 0,
                    "Calculation finished with light error. Attempts:"  + executor.getLightErrors());
            return;
        }
        if (result.get().isEmpty()) {
            System.out.println("F");
            clientSocketIO.sendData(name, x, Status.FATAL_ERROR, 0,
                    "Calculation finished with fatal error. Attempts: " + executor.getLightErrors());
            System.out.println("Fatal error " + x);
            return;
        }
        int fx = result.get().get();
        clientSocketIO.sendData(name, x, Status.SUCCESS, fx, "Calculation success! Result: " + fx);
    }


}
