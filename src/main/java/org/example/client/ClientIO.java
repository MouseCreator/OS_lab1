package org.example.client;

public interface ClientIO {
    ValueTimeoutRecord receiveValue();
    void  sendToServer(String status, String value);
}

record ValueTimeoutRecord(int x, long timeoutMillis) {}
