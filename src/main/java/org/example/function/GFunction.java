package org.example.function;

import java.util.Optional;

public class GFunction implements Function<Integer, Integer>{
    @Override
    public Optional<Optional<Integer>> compute(Integer arg) {
        try {
            int sqRoot = discreteSquareRoot(arg);
            return Optional.of(Optional.of(sqRoot));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private int discreteSquareRoot(Integer a) {
        if (a < 0) {
            throw new IllegalArgumentException("Cannot calculate square root of negative number: "+ a);
        }
        int sqRoot = 0;
        while (sqRoot * sqRoot <= a) {
            sqRoot++;
        }
        return sqRoot - 1;
    }
}
