package org.example.main.completable.socket;

import org.example.main.completable.calculation.CalculationParameters;
import org.example.main.completable.dto.ProcessRequestDTO;
import org.example.main.completable.dto.ProcessResponseDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

public class SocketManagerAtom {

    private ServerSocket serverSocket;

    private Socket FSocket;
    private Socket GSocket;
    private final LinkedBlockingQueue<CalculationParameters> fQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<CalculationParameters> gQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<ProcessResponseDTO> resultQueue = new LinkedBlockingQueue<>();

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public CompletableFuture<Integer> calculateF(CalculationParameters params) {
        return CompletableFuture.supplyAsync(()->runFuture(FSocket, params));
    }

    public CompletableFuture<Integer> calculateG(CalculationParameters params) {
        return CompletableFuture.supplyAsync(()->runFuture(GSocket, params));
    }

    private int runFuture(Socket clientSocket, CalculationParameters params) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            provideData(outputStream, params);
            return receiveData(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int receiveData(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        ProcessResponseDTO receivedProcessDTO = (ProcessResponseDTO) inputStream.readObject();
        System.out.println("Received from " + receivedProcessDTO.processName());
        System.out.println("Put result from " + receivedProcessDTO.processName());
        return calculate(receivedProcessDTO);
    }

    private int calculate(ProcessResponseDTO result) {
        if (result.processStatus()==0) {
            return result.value();
        } else {
            throw new RuntimeException("Calculation failed: " + result.details());
        }
    }

    private void provideData(ObjectOutputStream outputStream, CalculationParameters params)
            throws IOException {
        int x = params.x();
        long timeoutMillis = params.timeout();
        int limitIterations = params.limitIterations();
        outputStream.writeObject(new ProcessRequestDTO(x, timeoutMillis, limitIterations));
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

    public LinkedBlockingQueue<ProcessResponseDTO> getResultQueue() {
        return resultQueue;
    }
}
