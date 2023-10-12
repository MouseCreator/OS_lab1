package org.example.main.completable.socket;

import org.example.main.completable.calculation.CalculationParameters;
import org.example.main.completable.dto.FunctionInput;
import org.example.main.completable.dto.FunctionOutput;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

public class LongTermSocketManager {
    private ServerSocket serverSocket;
    private Socket FSocket;
    private Socket GSocket;
    private ObjectOutputStream outputStreamF;
    private ObjectInputStream inputStreamF;
    private ObjectOutputStream outputStreamG;
    private ObjectInputStream inputStreamG;

    private void initStreams() throws IOException {
        outputStreamF = new ObjectOutputStream(FSocket.getOutputStream());
        inputStreamF = new ObjectInputStream(FSocket.getInputStream());
        outputStreamG = new ObjectOutputStream(GSocket.getOutputStream());
        inputStreamG = new ObjectInputStream(GSocket.getInputStream());
    }

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

    public void accept() {
        try {
            FSocket = serverSocket.accept();
            GSocket = serverSocket.accept();
            initStreams();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public CompletableFuture<FunctionOutput> calculateF(CalculationParameters params) {
        return CompletableFuture.supplyAsync(()->runFuture(inputStreamF, outputStreamF, params));
    }

    public CompletableFuture<FunctionOutput> calculateG(CalculationParameters params) {
        return CompletableFuture.supplyAsync(()->runFuture(inputStreamG, outputStreamG, params));
    }

    private FunctionOutput runFuture(ObjectInputStream inputStream, ObjectOutputStream outputStream, CalculationParameters params) {
        try {
            provideData(outputStream, params);
            return receiveData(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private FunctionOutput receiveData(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        Object obj = inputStream.readObject();
        return  (FunctionOutput) obj;
    }

    private void provideData(ObjectOutputStream outputStream, CalculationParameters params)
            throws IOException {
        int x = params.x();
        long timeout = params.timeout();
        int signal = params.signal();
        outputStream.writeObject(new FunctionInput(x, timeout, signal));
        outputStream.flush();
    }

    public void close() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        close(FSocket);
        close(GSocket);
    }

    private void close(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
