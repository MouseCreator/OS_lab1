package org.example.util;

public class MathUtil {
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
