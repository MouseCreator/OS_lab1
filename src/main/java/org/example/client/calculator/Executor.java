package org.example.client.calculator;

import org.example.client.computation.Computation;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Executor {
    private final ResultDetails resultDetails = new ResultDetails();
    private final Computation computation;
    private final ReadWriteLock innerLock = new ReentrantReadWriteLock();
    public Executor(Computation computation) {
        this.computation = computation;
    }
    private int lightErrors = 0;
    public CompletableFuture<Optional<Optional<Integer>>> execute(int x) {
        return CompletableFuture.supplyAsync(() -> {
            resultDetails.start();
            Optional<Optional<Integer>> result = computation.compfunc(x);
            while (result.isEmpty()) {
                lightErrors++;
                result = computation.compfunc(x);
            }
            innerLock.writeLock().lock();
            try {
                if (result.get().isEmpty()) {
                    resultDetails.completedExceptionally();
                } else {
                    resultDetails.complete(result.get().get());
                }
            } finally {
                innerLock.readLock().unlock();
            }
            return result;
        });
    }

    public int getLightErrors() {
        return lightErrors;
    }

    public String status() {
        innerLock.readLock().lock();
        try {
            if (resultDetails.hasResult()) {
                return resultDetails.getDetails();
            }
            Long timeStarted = resultDetails.getTimeStartedMillis();
            Long timeNow = System.currentTimeMillis();
            long timeDifference = timeNow - timeStarted;
            return "Calculation in progress. Task is running for " + timeDifference + " ms. Total light errors: " + lightErrors;
        } finally {
            innerLock.readLock().unlock();
        }
    }

    public boolean isCompleted() {
        try {
            innerLock.readLock().lock();
            return resultDetails.hasResult();
        } finally {
            innerLock.readLock().unlock();
        }
    }

}
