package org.example.client.calculator;

import org.example.client.computation.Computation;
import org.example.client.socket.*;
import org.example.main.completable.dto.FunctionInput;
import org.example.main.completable.dto.Signal;
import org.example.main.completable.dto.Status;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.*;

public class LongTermCalculator implements CommonCalculator{
    private final LongTermClientSocketIO clientSocketIO;
    private final Computation computation;
    private ExecutorService pool;
    private final String name;
    private final HashMap<Integer, Executor> executorHashMap = new HashMap<>();
    public LongTermCalculator(LongTermClientSocketIO clientSocketIO, Computation computation, String name) {
        this.clientSocketIO = clientSocketIO;
        this.computation = computation;
        this.name = name;
        initPool();
    }

    /**
     * Main client (F or G) loop
     */
    public void calculate() {
        while (!Thread.interrupted()) {
            FunctionInput input = clientSocketIO.receiveData();
            int x = input.value();
            int signal = input.signal();
            switch (signal) {
                case Signal.CONTINUE -> executeAsync(input);
                case Signal.RESTART -> interruptAll();
                case Signal.SHUTDOWN -> {stopExecution(); return;}
                case Signal.STATUS -> getStatus(x);
                case Signal.STATUS_ALL -> getStatusAll();
            }

        }
    }

    /**
     * Creates new thread pool
     */
    private void initPool() {
        pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()-1);
    }

    /**
     * cancels all calculations
     */
    private void interruptAll() {
        pool.shutdownNow();
        initPool();
    }

    /**
     * Closes the thread pool
     */
    private void stopExecution() {
        pool.shutdownNow();
        pool.close();
    }

    /**
     * Calculates function at {@param input} in thread pool and sends result to server
     */
    private void executeAsync(FunctionInput input) {
        pool.submit(() -> executeComputation(input));
    }
    /**
     * Calculates function at {@param input} and sends result to server
     */
    private void executeComputation(FunctionInput input) {
        int x = input.value();
        long timeout = input.timeout();
        Executor executor = new Executor(computation);
        try {
            executorHashMap.put(x, executor);
            computeFunctionAt(x, timeout, executor);
        } catch (ExecutionException e) {
            clientSocketIO.sendData(name, x, Status.CRITICAL_ERROR, 0,
                    "Calculation finished with execution error");
        } catch (TimeoutException e) {
            clientSocketIO.sendData(name, x, Status.TIMEOUT, 0,
                    "Execution timeout. Total light errors: " + executor.getLightErrors());
        } catch (InterruptedException e) {
            clientSocketIO.sendData(name, x, Status.INTERRUPT, 0,
                    "Calculation was interrupted. Total light errors: " + executor.getLightErrors());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Sends statuses of all calculations to server
     */
    private void getStatusAll() {
        StringBuilder builder = new StringBuilder();
        for (Integer x : executorHashMap.keySet()) {
            Executor executor = executorHashMap.get(x);
            if (!executor.isCompleted()) {
                builder.append(x).append(": ").append(executor.status()).append('\n');
            }
        }
        clientSocketIO.sendData(name, -1, Status.STATUS_ALL, 0, builder.toString());
    }

    /**
     *
     * Sends statuses of {@param x} calculation to server
     */
    private void getStatus(int x) {
        String status;
        Executor executor = executorHashMap.get(x);
        if (executor == null) {
            status = "Function at " + x + " was never calculated";
        } else {
            status = executor.status();
        }
        clientSocketIO.sendData(name, x, Status.STATUS, 0, status);
    }

    /**
     * Computes function with executor
     * @param x - input argument
     * @param timeout - calculation timeout
     * @param executor - executor
     * @throws InterruptedException - if was interrupted
     * @throws ExecutionException - if function was computed exceptionally
     * @throws TimeoutException - if calculation timeout
     */
    private void computeFunctionAt(int x, long timeout, Executor executor) throws InterruptedException, ExecutionException, TimeoutException {
        Optional<Optional<Integer>> result = executor.execute(x).get(timeout, TimeUnit.MILLISECONDS);
        if (result.isEmpty()) {
            clientSocketIO.sendData(name, x, Status.LIGHT_ERROR_LIMIT, 0,
                    "Calculation finished with light error. Attempts:"  + executor.getLightErrors());
            return;
        }
        if (result.get().isEmpty()) {
            clientSocketIO.sendData(name, x, Status.CRITICAL_ERROR, 0,
                    "Calculation finished with critical error. Attempts: " + executor.getLightErrors());
            return;
        }
        int fx = result.get().get();
        clientSocketIO.sendData(name, x, Status.SUCCESS, fx, "Calculation success! Result: " + fx);
    }


}
