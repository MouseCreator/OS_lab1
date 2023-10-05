package org.example.main.completable.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
    public CompletableFuture<Integer> sendX() {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        future.cancel(true);

        Socket clientSocket;
        try {
            clientSocket = serverSocket.accept();
            return runFuture(clientSocket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CompletableFuture<Integer> runFuture(Socket clientSocket) {
        return CompletableFuture.supplyAsync(()->{
            try {
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                readWriteLock.readLock().lock();
                writer.println(x);
                writer.println(timeoutMillis);
                readWriteLock.readLock().unlock();
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String status = reader.readLine();
                String details = reader.readLine();
                if (status.equals("0")) {
                    return Integer.parseInt(details);
                } else {
                    throw new RuntimeException(details);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                readWriteLock.readLock().unlock();
            }
        });
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
