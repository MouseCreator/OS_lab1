package org.example.client;

import org.example.function.Function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;

public class CommonCalculatorImpl implements CommonCalculator{
    private record ValueTimeoutRecord(int x, long timeoutMillis) {}
    public void calculate(Function<Integer, Integer> function, String name) {
        ValueTimeoutRecord valueTimeoutRecord = receiveValue();
        int x = valueTimeoutRecord.x();
        long timeout = valueTimeoutRecord.timeoutMillis;

        int lightErrorCount = 0;
        long begin = System.currentTimeMillis();
        do {
            Optional<Optional<Integer>> result = function.compute(x);
            if (result.isEmpty()) { //fatal error
                sendToServer("1", name + "finished with fatal error!");
                throw new RuntimeException();
            }
            if (result.get().isEmpty()) { //light error
                lightErrorCount++;
                continue;
            }
            int fx = result.get().get(); //success
            sendToServer("0", String.valueOf(fx));
        } while (notTimedOut(begin, timeout));

        // if timeout
        sendToServer("2", name + " calculation timeout. Total light errors: " + lightErrorCount);
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
