package org.example.client;

import org.example.function.GFunction;

import java.io.IOException;

public class ProcessG {
    public static void main(String[] args) {
        try {
            CommonCalculator commonCalculator = new CommonCalculatorImpl();
            SocketClientIo socketClientIo = new SocketClientIo();
            socketClientIo.start();
            commonCalculator.calculate(socketClientIo, new GFunction(), "Process G");
            socketClientIo.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
