package org.example.client;

import org.example.function.FFunction;

public class ProcessF {
    public static void main(String[] args) {
        CommonCalculator commonCalculator = new CommonCalculatorImpl();
        commonCalculator.calculate(new FFunction(), "Process F");
    }


}
