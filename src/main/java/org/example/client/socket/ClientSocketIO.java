package org.example.client.socket;



public interface ClientSocketIO {
    ValueTimeoutRecord receiveData(String name);
    void sendData(String name, int origin, int status, int result, String details);
}
