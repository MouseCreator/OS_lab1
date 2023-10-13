package org.example.client.computation;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Computation {
    private final int CASE1_ATTEMPTS = 3;
    private int attempt = CASE1_ATTEMPTS;
    private final int returnValue;
    public Computation(int returnValue) {
        this.returnValue = returnValue;
    }

    public Optional<Optional<Integer>> compfunc(int n) {
        switch (n) {
            case 0 -> {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException ie) {
                    return Optional.of(Optional.empty());
                }
                return Optional.of(Optional.empty());
            }
            case 1 -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ie) {
                    return Optional.of(Optional.empty());
                }
                attempt--;
                if (attempt != 0)
                    return Optional.empty();
                attempt = CASE1_ATTEMPTS;
                return Optional.of(Optional.of(returnValue));
            }
            case 2 -> {
                try {
                    TimeUnit.SECONDS.sleep(15);
                    return Optional.of(Optional.of(8));
                } catch (InterruptedException ie) {
                    return Optional.of(Optional.empty());
                }
            }
            case 3 -> {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    return Optional.of(Optional.of(9));
                } catch (InterruptedException ie) {
                    return Optional.of(Optional.empty());
                }
            }
            case 4 -> {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    return Optional.of(Optional.of(20));
                } catch (InterruptedException ie) {
                    return Optional.of(Optional.empty());
                }
            }
            default -> {
            }
        }

        try { Thread.currentThread().join(); } catch (InterruptedException ie) {}
        return Optional.of(Optional.empty());
    }

}
