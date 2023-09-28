package org.example.function;

import java.util.Optional;

public class FFunction implements Function<Integer, Integer> {
    @Override
    public Optional<Optional<Integer>> compute(Integer arg) {
        try {
            int result = discreteLog(arg);
            return Optional.of(Optional.of(result));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private int discreteLog(int a) {
        if (a < 0) {
            throw new IllegalArgumentException("Cannot calculate discrete log for negative number: " + a);
        }
        if (a == 0) {
            throw new IllegalArgumentException("Cannot calculate discrete log for zero");
        }
        int count = 0;
        while (a >= 2) {
            a = a >>> 1;
            count++;
        }
        return count;
    }
}
