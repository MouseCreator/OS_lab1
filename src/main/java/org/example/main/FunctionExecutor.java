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

        fResult.execute(this::receiveResultValue);
        gResult.execute(this::receiveResultValue);

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
            writer.print(x);
            writer.print(timeout);

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

    public int execute(int x, long t) throws IOException {
        start();
        int result = run(x, t);
        close();
        return result;
    }

    private void startFProcess() {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "out/artifacts/OS_lab1_jar/OS_lab1.jar");
        try {
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            // Read the output of the process
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println(exitCode);
        } catch (IOException | InterruptedException e) {
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
