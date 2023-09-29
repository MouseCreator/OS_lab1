package org.example.client;

import org.example.function.Function;

public interface CommonCalculator {
    void calculate(ClientIO clientIO, Function<Integer, Integer> function, String name);
}
