package org.example.main.completable.socket;

import org.example.main.completable.dto.ProcessRequestDTO;
import org.example.main.completable.dto.ProcessResponseDTO;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SocketManagerImpl implements SocketManager {
    private ServerSocket serverSocket;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
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

    private int x;
    private long timeoutMillis;
    public void set(int x, long timeout) {
        try {
            readWriteLock.writeLock().lock();
            this.x = x;
            this.timeoutMillis = timeout;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
    public void setX(int x) {
        try {
            readWriteLock.writeLock().lock();
            this.x = x;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
    public void setTimeoutMillis(long t) {
        try {
            readWriteLock.writeLock().lock();
            this.timeoutMillis = t;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
    public void doServerCycle() {
        while (!Thread.interrupted()) {
            handleClient();
        }
    }
    public void handleClient() {
        Socket clientSocket;
        try {
            clientSocket = serverSocket.accept();
            Thread clintThread = new Thread(() -> runFuture(clientSocket));
            clintThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void runFuture(Socket clientSocket) {
        CompletableFuture.supplyAsync(()->{
            try {
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                Object method = inputStream.readObject();
                if (method.equals("POST")) {
                    return receiveData(inputStream);
                } else if (method.equals("GET")) {
                    provideData(outputStream);
                } else {
                    throw new RuntimeException("Unknown method from client: " + method);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return 0;
        });
    }

    private int receiveData(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        ProcessResponseDTO receivedProcessDTO = (ProcessResponseDTO) inputStream.readObject();
        if (receivedProcessDTO.processStatus() == 0) {
            return receivedProcessDTO.value();
        } else {
            throw new RuntimeException(receivedProcessDTO.details());
        }
    }

    private void provideData(ObjectOutputStream outputStream) throws IOException {
        try {
            readWriteLock.readLock().lock();
            outputStream.writeObject(new ProcessRequestDTO(x, timeoutMillis));
            outputStream.flush();
        } finally {
            readWriteLock.readLock().unlock();
        }
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
}
