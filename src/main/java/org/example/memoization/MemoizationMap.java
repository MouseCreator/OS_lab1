package org.example.memoization;

import java.util.HashMap;
import java.util.Optional;

/**
 * Memoization map
 * @param <V> - argument type x
 * @param <R> - output type f(x)
 */
public class MemoizationMap<V, R> {
    private final HashMap<V, R> processMemoizationMap = new HashMap<>();

    /**
     * @return function output at {@param input} or empty optional if absent
     */
    public synchronized Optional<R> get(V input) {
        return Optional.ofNullable(processMemoizationMap.get(input));
    }

    /**
     *
     * @param input - argument
     * @param output - result
     * @return previous result
     */
    public synchronized Optional<R> put(V input, R output) {
        R prev = processMemoizationMap.put(input, output);
        return Optional.ofNullable(prev);
    }

    /**
     *
     * @param input - argument
     * @return true, if was calculated
     */
    public synchronized boolean isComputed(V input) {
        return processMemoizationMap.containsKey(input);
    }

    /**
     * Clears the map
     */
    public synchronized void clear() {
        processMemoizationMap.clear();
    }

    /**
     * removes argument if present
     * @param at - argument to be removed
     */
    public void remove(V at) {
        processMemoizationMap.remove(at);
    }
}
