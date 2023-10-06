package org.example.memoization;

import java.util.HashMap;
import java.util.Optional;

public class MemoizationMap<V> {
    private final HashMap<V, String> processMemoizationMap = new HashMap<>();

    public Optional<String> get(V input) {
        return Optional.ofNullable(processMemoizationMap.get(input));
    }

    public Optional<String> put(V input, String output) {
        String prev = processMemoizationMap.put(input, output);
        return Optional.ofNullable(prev);
    }

    public boolean isComputed(V input) {
        return processMemoizationMap.containsKey(input);
    }


}
