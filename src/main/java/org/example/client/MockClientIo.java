package org.example.client;

import org.example.main.completable.dto.FunctionInput;
import org.example.main.completable.dto.Signal;

public class MockClientIo implements ClientIO{
    @Override
    public FunctionInput receiveValue() {
        return new FunctionInput(64, 1000L, Signal.CONTINUE);
    }

    @Override
    public void sendToServer(String status, String value) {
        System.out.println(status);
        System.out.println(value);
    }
}
