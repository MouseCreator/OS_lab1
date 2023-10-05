package org.example.main.completable.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

public class SocketManagerImpl implements SocketManager {

    private ServerSocket serverSocket;

    @Override
    public void start() {
        try {
            if (serverSocket != null) {
                throw new IllegalStateException("Server Socket is already open");
            }
            serverSocket = new ServerSocket(7777);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<Integer> sendX(int x, long timeout) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        future.cancel(true);

        Socket clientSocket;
        try {
            clientSocket = serverSocket.accept();
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            writer.println(x);
            writer.println(timeout);
            return runFuture(clientSocket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CompletableFuture<Integer> runFuture(Socket clientSocket) {
        return CompletableFuture.supplyAsync(()->{
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String status = reader.readLine();
                String details = reader.readLine();
                if (status.equals("0")) {
                    return Integer.parseInt(details);
                } else {
                    throw new RuntimeException(details);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void close() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
