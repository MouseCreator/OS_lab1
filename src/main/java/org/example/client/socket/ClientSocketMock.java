package org.example.client.socket;

import org.example.main.completable.dto.FunctionInput;
import org.example.main.completable.dto.Signal;

public class ClientSocketMock implements LongTermClientSocketIO{

    private int sentDataCount = 0;
    @Override
    public FunctionInput receiveData() {
        sentDataCount++;
        return switch (sentDataCount) {
            case 1, 4 -> new FunctionInput(0, 6000L, Signal.CONTINUE);
            case 2, 5 -> new FunctionInput(1, 6000L,  Signal.CONTINUE);
            case 3 -> new FunctionInput(2, 6000L,  Signal.RESTART);
            case 6 -> new FunctionInput(3, 6000L,  Signal.SHUTDOWN);
            default ->  throw new RuntimeException("Too many iterations!");
        };

    }

    @Override
    public void sendData(String name, int origin, int status, int result, String details) {
        System.out.printf("%s, %d: status %d, result %d, details: %s\n", name, origin, status, result, details);
    }

    @Override
    public void connect() {
        System.out.println("Started!");
    }

    @Override
    public void close() {
        System.out.println("Closed!");
    }
}
