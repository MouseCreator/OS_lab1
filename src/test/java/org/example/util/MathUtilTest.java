package org.example.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MathUtilTest {
    private MathUtil mathUtil;

    @BeforeEach
    void setUp() {
        mathUtil = new MathUtil();
    }
    @Test
    void gcd() {
        assertEquals(10, mathUtil.gcd(10, 20));
        assertEquals(15, mathUtil.gcd(45, 60));
    }

    @Test
    void gcdErr() {
        assertEquals(-1, mathUtil.gcd(-20, 30));
        assertEquals(-1, mathUtil.gcd(20, -30));
    }
}