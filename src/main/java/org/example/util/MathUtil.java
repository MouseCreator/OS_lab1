package org.example.util;

/**
 * Math utility
 */
public class MathUtil {
    /**
     * Calculates gcd of two numbers
     * Uses Euclidean algorithm
     * @param a - number 1
     * @param b - number 2
     * @return gcd of a and b or -1 if values are negative
     */
    public int gcd(int a, int b) {

        if (a < 1 || b < 1)
            return -1;

        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}
