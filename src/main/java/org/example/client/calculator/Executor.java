package org.example.client.calculator;

import org.example.client.computation.Computation;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Executor {
    private final Computation computation;
    public Executor(Computation computation) {
        this.computation = computation;
    }

    public CompletableFuture<Optional<Optional<Integer>>> execute(int x) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Optional<Integer>> result = Optional.empty();
            while (result.isEmpty()) {
                result = computation.compfunc(x);
            }
            return result;
        });
    }

    public int getLightErrors() {
        return computation.errorCount();
    }
}