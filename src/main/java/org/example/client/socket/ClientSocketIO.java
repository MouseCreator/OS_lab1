package org.example.client.socket;



public interface ClientSocketIO {
    ValueTimeoutRecord receiveData();
    void sendData(String name, int status, int result, String details);
}
