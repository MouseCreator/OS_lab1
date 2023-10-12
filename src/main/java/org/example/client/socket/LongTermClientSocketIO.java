package org.example.client.socket;

public interface LongTermClientSocketIO extends ClientSocketIO {
    void connect();
    void close();
}
