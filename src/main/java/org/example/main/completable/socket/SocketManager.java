package org.example.main.completable.socket;

public interface SocketManager extends AutoCloseable {
    void start();
    void close();
}
