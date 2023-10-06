package org.example.client.calculator;

import org.example.client.socket.ClientSocketIO;
import org.example.client.socket.ClientSocketManager;
import org.example.client.socket.ValueTimeoutRecord;
import org.example.function.Function;
import org.example.main.completable.dto.Status;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CommonCalculatorSocket implements CommonCalculator {
    private final ClientSocketIO clientSocketIO = new ClientSocketManager();
    public void calculate(Function<Integer, Integer> function, String name) {
        ValueTimeoutRecord valueTimeoutRecord = clientSocketIO.receiveData(name);
        int x = valueTimeoutRecord.x();
        long timeout = valueTimeoutRecord.timeoutMillis();
        int limit = valueTimeoutRecord.limit();
        Executor executor = new Executor(x, limit, function);
        try {
            Optional<Optional<Integer>> result = executor.execute().get(timeout, TimeUnit.MILLISECONDS);
            System.out.println("Got result");
            if (result.isEmpty()) {
                clientSocketIO.sendData(name, x, Status.FATAL_ERROR, 0,
                        "Calculation finished with error");
                return;
            }
            if (result.get().isEmpty()) {
                clientSocketIO.sendData(name, x, Status.LIGHT_ERROR_LIMIT, 0,
                        "Calculation finished with light error");
                return;
            }
            int fx = result.get().get();
            clientSocketIO.sendData(name, x, Status.SUCCESS, fx, "Calculation finished with light error");
        } catch (ExecutionException e) {
            clientSocketIO.sendData(name, x, Status.FATAL_ERROR, 0,
                    "Calculation finished with execution error");
        } catch (TimeoutException e) {
            clientSocketIO.sendData(name, x, Status.TIMEOUT, 0,
                    "Execution timeout. Total light errors: " + executor.getLightErrors());
        } catch (InterruptedException e) {
            clientSocketIO.sendData(name, x, Status.INTERRUPT, 0, "Calculation was interrupted");
        }

    }
    private static class Executor {

        private final int x;
        private final Function<Integer, Integer> function;
        private final int limit;
        private int lightErrors;
        public Executor(int x, int limit, Function<Integer, Integer> function) {
            this.x = x;
            lightErrors = 0;
            this.limit = limit;
            this.function = function;
        }

        private CompletableFuture<Optional<Optional<Integer>>> execute() {
            return CompletableFuture.supplyAsync(() -> {

                for (int i = 0; i < limit; i++) {
                    Optional<Optional<Integer>> tempResult = function.compute(x);
                    if (tempResult.isEmpty()) {
                        return tempResult;
                    }
                    if (tempResult.get().isEmpty()) {
                        lightErrors++;
                        continue;
                    }
                    return tempResult;

                }
                return Optional.of(Optional.empty());
            });
        }

        public int getLightErrors() {
            return lightErrors;
        }
    }
}
