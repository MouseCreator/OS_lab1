package org.example.client;

import org.example.function.Function;
import org.example.promise.Promise;
import org.example.promise.PromiseImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.*;

public class CommonCalculatorImpl implements CommonCalculator{
    private record ValueTimeoutRecord(int x, long timeoutMillis) {}
    public void calculate(Function<Integer, Integer> function, String name) {
        ValueTimeoutRecord valueTimeoutRecord = receiveValue();
        int x = valueTimeoutRecord.x();

        Promise<Optional<Optional<Integer>>> promise = new PromiseImpl<>();
        CalculationRunnable task = new CalculationRunnable(function, x);
        promise.execute(task);

        long timeout = valueTimeoutRecord.timeoutMillis();

        try {
            Optional<Optional<Integer>> result = promise.get(timeout);
            if (result.isEmpty()) {
                sendToServer("1", name + ": calculation finished with error");
                return;
            }
            if (result.get().isEmpty()) {
                sendToServer("2", name + ": calculation finished with light error");
                return;
            }
            int fx = result.get().get();
            sendToServer("0", String.valueOf(fx));
        } catch (InterruptedException e) {
            sendToServer("3", name + ": calculation was interrupted");
        } catch (ExecutionException e) {
            sendToServer("4", name + ": finished with execution error " + e.getCause().getMessage());
        } catch (TimeoutException e) {
            sendToServer("5", name + ": execution timeout. Total light errors: " + task.lightErrorCount);
        }


    }
    private static final Object object = new Object();
    public synchronized void setResult(int fx) {
        object.notify();
    }

    private static class CalculationRunnable implements Callable<Optional<Optional<Integer>>> {
        private final Function<Integer, Integer> function;
        private final int x;
        private int lightErrorCount = 0;

        public CalculationRunnable(Function<Integer, Integer> function, int x) {
            this.function = function;
            this.x = x;
        }

        public int getLightErrorCount() {
            return lightErrorCount;
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

    private boolean notTimedOut(long beginTime, long timeout) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - beginTime;
        return elapsedTime < timeout;
    }

    private void sendToServer(String status, String message) {
        try (Socket clientSocket = new Socket("127.0.0.1", 7777)) {
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            writer.println(status);
            writer.println(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ValueTimeoutRecord receiveValue() {
        try (Socket clientSocket = new Socket("127.0.0.1", 7777)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String serverMessage;
            serverMessage = reader.readLine();
            int x = Integer.parseInt(serverMessage);
            serverMessage = reader.readLine();
            long timeoutMillis = Long.parseLong(serverMessage);
            return new ValueTimeoutRecord(x, timeoutMillis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
