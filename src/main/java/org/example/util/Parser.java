package org.example.util;

import java.util.Optional;

/**
 * User input parser utility
 */
public class Parser {
    /**
     *
     * @param s - string to parse
     * @return parsed value or empty optional if value cannot be parser to int
     */
    public static Optional<Integer> toInteger(String s) {
        try {
            int value = Integer.parseInt(s);
            return Optional.of(value);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    /**
     *
     * @param s - string to parse
     * @return parsed value or empty optional if value cannot be parser to long
     */
    public static Optional<Long> toLong(String s) {
        try {
            long value = Long.parseLong(s);
            return Optional.of(value);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
