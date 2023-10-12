package org.example.client;

import org.example.main.completable.dto.FunctionInput;

public interface ClientIO {
    FunctionInput receiveValue();
    void  sendToServer(String status, String value);
}


