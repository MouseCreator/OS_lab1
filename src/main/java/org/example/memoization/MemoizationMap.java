package org.example.memoization;

import java.util.HashMap;
import java.util.Optional;

public class MemoizationMap<V> {
    private final HashMap<V, V> processMemoizationMap = new HashMap<>();

    public Optional<V> get(V input) {
        return Optional.ofNullable(processMemoizationMap.get(input));
    }

    public Optional<V> put(V input, V output) {
        V prev = processMemoizationMap.put(input, output);
        return Optional.ofNullable(prev);
    }

    public boolean isComputed(V input) {
        return processMemoizationMap.containsKey(input);
    }


}
