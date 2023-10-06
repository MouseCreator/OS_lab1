package org.example.client;

import org.example.client.socket.ValueTimeoutRecord;

public class MockClientIo implements ClientIO{
    @Override
    public ValueTimeoutRecord receiveValue() {
        return new ValueTimeoutRecord(64, 1000L, 0);
    }

    @Override
    public void sendToServer(String status, String value) {
        System.out.println(status);
        System.out.println(value);
    }
}
