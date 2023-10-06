package org.example.client;

import org.example.client.calculator.CommonCalculator;
import org.example.client.calculator.CommonCalculatorSocket;
import org.example.function.FFunction;

public class ProcessF {
    public static void main(String[] args) {
        while (!Thread.interrupted()) {
            CommonCalculator commonCalculator = new CommonCalculatorSocket();
            System.out.println("F starts!");
            commonCalculator.calculate(new FFunction(), "Process F");
        }
    }


}
