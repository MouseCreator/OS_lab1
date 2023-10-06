package org.example.client.calculator;

import org.example.function.Function;

public interface CommonCalculator {
    void calculate(Function<Integer, Integer> function, String name);
}
