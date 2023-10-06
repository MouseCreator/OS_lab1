package org.example.main.completable.socket;

import org.example.main.completable.calculation.CalculationParameters;
import org.example.main.completable.dto.ProcessRequestDTO;
import org.example.main.completable.dto.ProcessResponseDTO;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class SocketManagerImpl implements SocketManager {
    private ServerSocket serverSocket;
    private final LinkedBlockingQueue<CalculationParameters> fQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<CalculationParameters> gQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<ProcessResponseDTO> resultQueue = new LinkedBlockingQueue<>();

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

    @Override
    public void set(CalculationParameters calculationParameters) {
        System.out.println("Added " + calculationParameters.x());
        synchronized (fQueue) {
            fQueue.add(calculationParameters);
            fQueue.notifyAll();
        } synchronized (gQueue) {
            gQueue.add(calculationParameters);
            gQueue.notifyAll();
        }
    }
    public void doServerCycle() {
        while (!Thread.interrupted() && !serverSocket.isClosed()) {
            try {
                handleClient();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void handleClient() throws IOException {
        Socket clientSocket = serverSocket.accept();
        new Thread(() -> runFuture(clientSocket)).start();
    }

    private void runFuture(Socket clientSocket) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            Object method = inputStream.readObject();
            if (method.equals("POST")) {
                receiveData(inputStream);
            } else if (method.equals("GET")) {
                provideData(inputStream, outputStream);
            } else {
                throw new RuntimeException("Unknown method from client: " + method);
            }
        } catch (InterruptedException e) {
            return;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void receiveData(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        ProcessResponseDTO receivedProcessDTO = (ProcessResponseDTO) inputStream.readObject();
        System.out.println("Received from " + receivedProcessDTO.processName());
        synchronized (resultQueue) {
            resultQueue.add(receivedProcessDTO);
            resultQueue.notifyAll();
        }
        System.out.println("Put result from " + receivedProcessDTO.processName());
    }

    private void provideData(ObjectInputStream inputStream, ObjectOutputStream outputStream) throws IOException, ClassNotFoundException, InterruptedException {
        String name = (String) inputStream.readObject();
        CalculationParameters params;

        if (name.equals("Process F")) {
            synchronized (fQueue) {
                while (fQueue.isEmpty()) {
                    System.out.println("Waits on F");
                    fQueue.wait();
                }
                params = fQueue.poll();
            }
        } else if (name.equals("Process G")) {
            synchronized (gQueue) {
                while (gQueue.isEmpty()) {
                    System.out.println("Waits on G");
                    gQueue.wait();
                }
                params = gQueue.poll();
            }
        } else {
            throw new IOException("Unknown connection: " + name);
        }
        System.out.println("Provided to " + name);
        if (params == null) {
            System.out.println("EMPTY QUEUE!");
            throw new IOException("No data to provide");
        }
        int x = params.x();
        long timeoutMillis = params.timeout();
        int limitIterations = params.limitIterations();
        outputStream.writeObject(new ProcessRequestDTO(x, timeoutMillis, limitIterations));
        outputStream.flush();
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

    public LinkedBlockingQueue<ProcessResponseDTO> getResultQueue() {
        return resultQueue;
    }
}
