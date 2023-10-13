package org.example.client.calculator;

import org.example.client.ClientIO;
import org.example.client.MockClientIo;
import org.example.client.computation.Computation;
import org.example.main.completable.dto.FunctionInput;
import org.example.promise.Promise;
import org.example.promise.PromiseImpl;

import java.util.Optional;
import java.util.concurrent.*;

public class CommonCalculatorImpl implements CommonCalculator{

    private final ClientIO clientIO = new MockClientIo();
    private final Computation computation;
    private final String name;

    public CommonCalculatorImpl(Computation computation, String name) {
        this.computation = computation;
        this.name = name;
    }

    public void calculate() {
        FunctionInput input = clientIO.receiveValue();
        int x = input.value();
        Promise<Optional<Optional<Integer>>> promise = new PromiseImpl<>();
        CalculationRunnable task = new CalculationRunnable(computation, x);
        promise.execute(task);
        long timeout = input.timeout();

        try {
            Optional<Optional<Integer>> result = promise.get(timeout);
            if (result.isEmpty()) {
                clientIO.sendToServer("1", name + ": calculation finished with error");
                return;
            }
            if (result.get().isEmpty()) {
                clientIO.sendToServer("2", name + ": calculation finished with light error");
                return;
            }
            int fx = result.get().get();
            clientIO.sendToServer("0", String.valueOf(fx));
        } catch (InterruptedException e) {
            clientIO.sendToServer("3", name + ": calculation was interrupted");
        } catch (ExecutionException e) {
            clientIO.sendToServer("4", name + ": finished with execution error " + e.getCause().getMessage());
        } catch (TimeoutException e) {
            clientIO.sendToServer("5", name + ": execution timeout. Total light errors: " + task.lightErrorCount);
        }

    }

    private static class CalculationRunnable implements Callable<Optional<Optional<Integer>>> {
        private final Computation function;
        private final int x;
        private int lightErrorCount = 0;

        public CalculationRunnable(Computation computation, int x) {
            this.function = computation;
            this.x = x;
        }

        @Override
        public Optional<Optional<Integer>> call() {
            while (!Thread.interrupted()) {
                Optional<Optional<Integer>> result = function.compfunc(x);
                if (result.isEmpty()) //fatal error
                    return result;
                if (result.get().isEmpty()) { //light error
                    lightErrorCount++;
                    continue;
                }
                return result; //success
            }
            return Optional.empty();
        }
    }
}
