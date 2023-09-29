package org.example.client;

import org.example.function.FFunction;

public class ProcessF {
    public static void main(String[] args) {
        System.out.println("HELLO, WORLD!");
        CommonCalculator commonCalculator = new CommonCalculatorImpl();
        ClientIO socketClientIo = new SocketClientIo();
        commonCalculator.calculate(socketClientIo, new FFunction(), "Process F");
    }


}
