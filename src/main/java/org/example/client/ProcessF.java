package org.example.client;

import org.example.client.calculator.CommonCalculator;
import org.example.client.calculator.CommonCalculatorImpl;
import org.example.function.FFunction;

public class ProcessF {
    public static void main(String[] args) {
        try {
            CommonCalculator commonCalculator = new CommonCalculatorImpl();
            SocketClientIo socketClientIo = new SocketClientIo();
            socketClientIo.start();
            commonCalculator.calculate(new FFunction(), "Process F");
            socketClientIo.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
