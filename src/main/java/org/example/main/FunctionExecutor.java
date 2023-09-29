package org.example.main;

import org.example.promise.Promise;
import org.example.promise.PromiseImpl;
import org.example.util.MathUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public class FunctionExecutor {
    private ServerSocket serverSocket;
    public void start() throws IOException {
        serverSocket = new ServerSocket(7777);
    }

    public int run(int x, long timeout) {
        startFProcess();
        startGProcess();
        Promise<Integer> fResult = new PromiseImpl<>();
        fResult.execute(()->provideXValue(x, timeout));
        Promise<Integer> gResult = new PromiseImpl<>();
        gResult.execute(()->provideXValue(x, timeout));

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


    private Integer provideXValue(int x, long timeout) {
        Socket clientSocket;
        try {
            clientSocket = serverSocket.accept();
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            writer.println(x);
            writer.println(timeout);

            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String status = reader.readLine();
            String details = reader.readLine();
            if (status.equals("0")) {
                return Integer.parseInt(details);
            } else {
                throw new RuntimeException(details);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer receiveResultValue() throws Exception {
        Socket clientSocket;
        try {
            clientSocket = serverSocket.accept();
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String status = reader.readLine();
            if (status.equals("0")) {
                return Integer.parseInt(reader.readLine());
            } else {
                throw new Exception(reader.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void startFProcess() {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "out/artifacts/OS_lab1_jar/OS_lab1.jar");
        try {
            processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void startGProcess() {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "out/artifacts/OS_lab1_jar2/OS_lab1.jar");
        try {
            processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
