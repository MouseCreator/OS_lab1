package org.example.util;

import java.util.Optional;

public class Parser {
    public static Optional<Integer> toInteger(String s) {
        try {
            int value = Integer.parseInt(s);
            return Optional.of(value);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    public static Optional<Long> toLong(String s) {
        try {
            long value = Long.parseLong(s);
            return Optional.of(value);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
