package org.example.client;

import org.example.client.socket.ValueTimeoutRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClientIo implements ClientIO {

    private Socket socket = null;
    @Override
    public ValueTimeoutRecord receiveValue() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String serverMessage;
            serverMessage = reader.readLine();
            int x = Integer.parseInt(serverMessage);
            System.out.println(x);
            serverMessage = reader.readLine();
            long timeoutMillis = Long.parseLong(serverMessage);
            return new ValueTimeoutRecord(x, timeoutMillis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendToServer(String status, String value) {
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(status);
            writer.println(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() throws IOException {
        socket = new Socket("127.0.0.1", 7777);
    }
    public void close() throws IOException {
        if (socket != null)
            socket.close();
    }
}
