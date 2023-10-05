package org.example.client;

import org.example.client.socket.ValueTimeoutRecord;
import org.example.function.Function;
import org.example.promise.Promise;
import org.example.promise.PromiseImpl;

import java.util.Optional;
import java.util.concurrent.*;

public class CommonCalculatorImpl implements CommonCalculator{



    public void calculate(ClientIO clientIO, Function<Integer, Integer> function, String name) {
        ValueTimeoutRecord valueTimeoutRecord = clientIO.receiveValue();
        int x = valueTimeoutRecord.x();
        Promise<Optional<Optional<Integer>>> promise = new PromiseImpl<>();
        CalculationRunnable task = new CalculationRunnable(function, x);
        promise.execute(task);
        long timeout = valueTimeoutRecord.timeoutMillis();

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
        private final Function<Integer, Integer> function;
        private final int x;
        private int lightErrorCount = 0;

        public CalculationRunnable(Function<Integer, Integer> function, int x) {
            this.function = function;
            this.x = x;
        }

        @Override
        public Optional<Optional<Integer>> call() {
            while (!Thread.interrupted()) {
                Optional<Optional<Integer>> result = function.compute(x);
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
