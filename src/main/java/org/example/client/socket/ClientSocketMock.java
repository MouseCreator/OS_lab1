package org.example.client.socket;

public class ClientSocketMock implements ClientSocketIO{
    @Override
    public ValueTimeoutRecord receiveData() {
        return new ValueTimeoutRecord(64, 1000L);
    }

    @Override
    public void sendData(String name, int status, int result, String details) {
        System.out.printf("%s: status %d, result %d, details: %s\n", name, status, result, details);
    }
}
