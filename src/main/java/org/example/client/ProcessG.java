package org.example.client;

import org.example.client.calculator.CommonCalculator;
import org.example.client.calculator.CommonCalculatorImpl;
import org.example.function.GFunction;

import java.io.IOException;

public class ProcessG {
    public static void main(String[] args) {
        try {
            CommonCalculator commonCalculator = new CommonCalculatorImpl();
            SocketClientIo socketClientIo = new SocketClientIo();
            socketClientIo.start();
            commonCalculator.calculate(new GFunction(), "Process G");
            socketClientIo.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
