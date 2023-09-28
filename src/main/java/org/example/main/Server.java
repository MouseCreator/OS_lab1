package org.example.main;

import org.example.promise.Promise;
import org.example.promise.PromiseImpl;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

public class Server {

    private ServerSocket serverSocket;

    public void start() throws IOException {
        serverSocket = new ServerSocket(7777);
    }

    public void close() throws IOException {
        serverSocket.close();
    }


    private void provideXValue() {
        int x = 10;
        Socket clientSocket;
        try {
            clientSocket = serverSocket.accept();
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            writer.print(x);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readResult() {
        CompletableFuture<Integer> completableFuture;
        Promise<Integer> result = new PromiseImpl<>();
    }

    private class ProviderThread extends Thread {
        @Override
        public void run() {
            while (!interrupted()) {
                provideXValue();
            }
        }
    }

    private void startFProcess() {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "out/artifacts/OS_lab1_jar/OS_lab1.jar");
        Process process;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void startGProcess() {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "out/artifacts/OS_lab1_jar/OS_lab1.jar");
        Process process;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
