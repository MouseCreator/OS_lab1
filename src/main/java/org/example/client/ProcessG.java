package org.example.client;

import org.example.client.calculator.CommonCalculator;
import org.example.client.calculator.CommonCalculatorSocket;
import org.example.function.GFunction;

public class ProcessG {
    public static void main(String[] args) {
        while (!Thread.interrupted()) {
            CommonCalculator commonCalculator = new CommonCalculatorSocket();
            System.out.println("G starts!");
            commonCalculator.calculate(new GFunction(), "Process G");
        }
    }
}
