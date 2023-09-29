package org.example.client;

import org.example.function.Function;
import org.example.promise.Promise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommonCalculatorImpl implements CommonCalculator{
    private record ValueTimeoutRecord(int x, long timeoutMillis) {}
    public void calculate(Function<Integer, Integer> function, String name) {
        ValueTimeoutRecord valueTimeoutRecord = receiveValue();
        int x = valueTimeoutRecord.x();
        int N = 2;
        ExecutorService service = Executors.newFixedThreadPool(N);
        CalculationRunnable[] calculationRunnables = new CalculationRunnable[N];
        for (int i = 0; i < N; i++) {
            calculationRunnables[i] = new CalculationRunnable(function, x);
            service.submit(calculationRunnables[i]);
        }
        Optional<Optional<Integer>> result = Optional.empty();
        synchronized (object) {
            Promise<Optional<Optional<Integer>>> promise;

            if (result.isEmpty()) {
                sendToServer("1", name + "finished with fatal error!");
                return;
            } else if (result.get().isEmpty()) {
                sendToServer("2", name + "finished with light error!");
            } else {
                sendToServer("0", String.valueOf(result.get().get()));
            }

        }
        // if timeout
        service.shutdown();
        int totalLightErrorCount = 0;
        for (CalculationRunnable calculationRunnable : calculationRunnables) {
            int lightErrorCount = calculationRunnable.getLightErrorCount();
            totalLightErrorCount += lightErrorCount;
        }

        sendToServer("3", name + " calculation timeout. Total light errors: " + totalLightErrorCount);
    }
    private static final Object object = new Object();
    public synchronized void setResult(Optional<Optional<Integer>> fx) {

        object.notify();
    }

    private static class CalculationRunnable implements Runnable {
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
        public void run() {
            while (!Thread.interrupted()) {
                Optional<Optional<Integer>> result = function.compute(x);
                if (result.isEmpty()) { //fatal error

                    return;
                }
                if (result.get().isEmpty()) { //light error
                    lightErrorCount++;
                    continue;
                }
                int fx = result.get().get();
                return;
            }
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
