package org.example.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FFunctionTest {

    private FFunction function;

    @BeforeEach
    void setUp() {
        function = new FFunction();
    }

    @Test
    void compute() {

        for (int i = 1; i < 20; i++) {
            Optional<Optional<Integer>> logOptional = function.compute(i);
            assertTrue(logOptional.isPresent());
            assertTrue(logOptional.get().isPresent());
            int log = logOptional.get().get();
            int actual = 1;
            for (int j = 0; j < log; j++) {
                actual *= 2;
            }
            assertTrue(actual <= i, "Failed comparing: "+ actual + "<=" + i);
            assertTrue(actual * 2 > i, "Failed comparing: "+ actual * 2 + ">" + i);
        }
    }

    @Test
    void computeNegative() {
        Optional<Optional<Integer>> logNegative = function.compute(-3);
        assertTrue(logNegative.isEmpty());
    }
}