package org.example.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GFunctionTest {

    private GFunction function;

    @BeforeEach
    void setUp() {
        function = new GFunction();
    }

    @Test
    void compute() {

        for (int i = 1; i < 20; i++) {
            Optional<Optional<Integer>> logOptional = function.compute(i);
            assertTrue(logOptional.isPresent());
            assertTrue(logOptional.get().isPresent());
            int sqrt = logOptional.get().get();
            assertTrue(sqrt * sqrt <= i, "Failed comparing: "+ sqrt * sqrt + "<=" + i);
            assertTrue((sqrt+1) * (sqrt+1) > i, "Failed comparing: "+ (sqrt+1) * (sqrt+1) * 2 + ">" + i);
        }
    }

    @Test
    void computeNegative() {
        Optional<Optional<Integer>> logNegative = function.compute(-3);
        assertTrue(logNegative.isEmpty());
    }
}