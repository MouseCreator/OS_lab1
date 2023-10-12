package org.example.client.socket;


import org.example.main.completable.dto.FunctionInput;

public interface ClientSocketIO {
    FunctionInput receiveData();
    void sendData(String name, int origin, int status, int result, String details);
}
