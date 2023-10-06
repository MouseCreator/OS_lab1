package org.example.client.calculator;

import org.example.function.Function;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Executor {
    private final int x;
    private final Function<Integer, Integer> function;
    private final int limit;
    private int lightErrors;
    public Executor(int x, int limit, Function<Integer, Integer> function) {
        this.x = x;
        lightErrors = 0;
        this.limit = limit;
        this.function = function;
    }

    public CompletableFuture<Optional<Optional<Integer>>> execute() {
        return CompletableFuture.supplyAsync(() -> {

            for (int i = 0; i < limit; i++) {
                Optional<Optional<Integer>> tempResult = function.compute(x);
                if (tempResult.isEmpty()) {
                    return tempResult;
                }
                if (tempResult.get().isEmpty()) {
                    lightErrors++;
                    continue;
                }
                return tempResult;

            }
            return Optional.of(Optional.empty());
        });
    }

    public int getLightErrors() {
        return lightErrors;
    }
}
