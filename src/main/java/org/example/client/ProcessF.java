package org.example.client;

import org.example.function.FFunction;

public class ProcessF {
    public static void main(String[] args) {
        try {
            CommonCalculator commonCalculator = new CommonCalculatorImpl();
            SocketClientIo socketClientIo = new SocketClientIo();
            socketClientIo.start();
            commonCalculator.calculate(socketClientIo, new FFunction(), "Process F");
            socketClientIo.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
