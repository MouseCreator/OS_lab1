package org.example.client.socket;

public class ClientSocketMock implements ClientSocketIO{
    @Override
    public ValueTimeoutRecord receiveData() {
        return new ValueTimeoutRecord(64, 1000L, 10);
    }

    @Override
    public void sendData(String name, int origin, int status, int result, String details) {
        System.out.printf("%s, %d: status %d, result %d, details: %s\n", name, origin, status, result, details);
    }
}
