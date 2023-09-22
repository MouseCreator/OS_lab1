package org.example.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientProcessF {
    public static void main(String[] args) {

        try (Socket clientSocket = new Socket("127.0.0.1", 7777)) {
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            writer.println("Process F is running!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
