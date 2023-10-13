package org.example.client.socket;

import org.example.main.completable.dto.FunctionInput;
import org.example.main.completable.dto.FunctionOutput;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LongTermClientSocketManager implements LongTermClientSocketIO {
    private final static String address = "127.0.0.1";
    private final static int port = 7777;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    public void connect() {
        try {
            socket = new Socket(address, port);
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FunctionInput receiveData() {
        try {
            return (FunctionInput) inputStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void sendData(String name, int origin, int status, int result, String details) {
        try {
            System.out.println("Before");
            FunctionOutput response = new FunctionOutput(name, origin, status, result, details);
            outputStream.writeObject(response);
            System.out.println("After");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
