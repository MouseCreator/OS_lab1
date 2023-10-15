package org.example.client.calculator;

import org.example.client.computation.Computation;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class to manage function execution
 */
public class Executor {
    private final ResultDetails resultDetails = new ResultDetails();
    private final Computation computation;
    private final ReadWriteLock innerLock = new ReentrantReadWriteLock();
    public Executor(Computation computation) {
        this.computation = computation;
    }
    private int lightErrors = 0;

    /**
     * @param x - input argument
     * @return function value at {@param x}
     */
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
                innerLock.writeLock().unlock();
            }
            return result;
        });
    }

    /**
     *
     * @return number of light errors
     */
    public int getLightErrors() {
        return lightErrors;
    }

    /**
     *
     * @return string representation of calculation status
     */
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

    /**
     * @return true if calculation is done
     */
    public boolean isCompleted() {
        try {
            innerLock.readLock().lock();
            return resultDetails.hasResult();
        } finally {
            innerLock.readLock().unlock();
        }
    }

}
