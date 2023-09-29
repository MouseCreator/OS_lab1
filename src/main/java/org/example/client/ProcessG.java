package org.example.client;

import org.example.function.GFunction;

public class ProcessG {
    public static void main(String[] args) {
        CommonCalculator commonCalculator = new CommonCalculatorImpl();
        commonCalculator.calculate(new GFunction(), "Process G");
    }
}
