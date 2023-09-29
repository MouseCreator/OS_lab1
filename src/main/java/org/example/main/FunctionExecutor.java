package org.example.main;

import org.example.promise.Promise;
import org.example.promise.PromiseImpl;
import org.example.util.MathUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FunctionExecutor {
    private ServerSocket serverSocket;
    public void start() throws IOException {
        serverSocket = new ServerSocket(7777);
    }

    public int run(int x) {
        CompletableFuture<Process> fProcess = startFProcess();
        CompletableFuture<Process> gProcess = startGProcess();

        return awaitAndCalculate();
    }

    private int awaitAndCalculate() {
        PromiseTask promiseFTask = new PromiseTask(() -> null);
        Promise<Integer> fResult = promiseFTask.awaitResult();

        PromiseTask promiseGTask = new PromiseTask(() -> null);
        Promise<Integer> gResult = promiseGTask.awaitResult();

        MathUtil mathUtil = new MathUtil();

        try {
            int fx = fResult.get();
            int gx = gResult.get();
            return mathUtil.gcd(fx, gx);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
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

    private Optional<Integer> receiveResultValue() throws Exception {
        Socket clientSocket;
        try {
            clientSocket = serverSocket.accept();
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String status = reader.readLine();
            if (status.equals("0")) {
                int result = Integer.parseInt(reader.readLine());
                return Optional.of(result);
            } else {
                throw new Exception(reader.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readResult() {
        CompletableFuture<Integer> completableFuture;
        Promise<Integer> result = new PromiseImpl<>();
    }

    public int execute(int x) throws IOException {
        start();
        int result = run(x);
        close();
        return result;
    }

    private class ProviderThread extends Thread {
        @Override
        public void run() {
            while (!interrupted()) {
                provideXValue();
            }
        }
    }

    private CompletableFuture<Process> startFProcess() {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "out/artifacts/OS_lab1_jar/OS_lab1.jar");
        Process process;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return process.onExit();
    }
    private CompletableFuture<Process> startGProcess() {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "out/artifacts/OS_lab1_jar/OS_lab1.jar");
        Process process;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return process.onExit();
    }
}
