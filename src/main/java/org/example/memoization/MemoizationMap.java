package org.example.memoization;

import java.util.HashMap;
import java.util.Optional;

public class MemoizationMap<V, R> {
    private final HashMap<V, R> processMemoizationMap = new HashMap<>();

    public Optional<R> get(V input) {
        return Optional.ofNullable(processMemoizationMap.get(input));
    }

    public Optional<R> put(V input, R output) {
        R prev = processMemoizationMap.put(input, output);
        return Optional.ofNullable(prev);
    }

    public boolean isComputed(V input) {
        return processMemoizationMap.containsKey(input);
    }


}
