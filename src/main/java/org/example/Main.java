package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        //start F and G
        //Ask for X value (read)
        //F and G process and write the result
        //Memoization
        //Promise`

        runSimpleProcess();
        try {
            try (ServerSocket serverSocket = new ServerSocket(7777)) {
                Socket clientSocket = serverSocket.accept();

                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String clientMessage;
                while ((clientMessage = reader.readLine()) != null) {
                    System.out.println("Request: " + clientMessage);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runSimpleProcess() {
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "out/artifacts/OS_lab1_jar/OS_lab1.jar");
        Process process;
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}