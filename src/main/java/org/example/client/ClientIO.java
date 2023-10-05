package org.example.client;

import org.example.client.socket.ValueTimeoutRecord;

public interface ClientIO {
    ValueTimeoutRecord receiveValue();
    void  sendToServer(String status, String value);
}


