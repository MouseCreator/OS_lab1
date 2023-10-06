package org.example.main.completable.calculation;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Calculation<T> {
    private final CompletableFuture<T> future;
    private final String name;
    private String error = "";
    private final Object sync = new Object();
    public Calculation(CompletableFuture<T> future, String name) {
        this.future = future;
        this.name = name;
    }

    public String printState() {
        Future.State state = future.state();
        return switch (state) {
            case RUNNING -> name + " is running";
            case FAILED -> name + " failed";
            case SUCCESS -> name + "  finished";
            case CANCELLED -> name + " is cancelled";
        };
    }

    public Future.State state() {
        return future.state();
    }

    public void fail(String message) {
        error = message;
        future.completeExceptionally(new Throwable(message));
    }

    public String getErrorMessage() {
        return error;
    }
    public void cancel() {
        future.cancel(true);
    }
    public T get() throws ExecutionException, InterruptedException {
        return future.get();
    }

    public T waitAndGet() throws ExecutionException, InterruptedException {
        synchronized (sync) {
            while (!future.isDone()) {
                sync.wait();
            }
            return future.get();
        }
    }
    public void finish() {
        assert future.isDone();
        synchronized (sync) {
            sync.notifyAll();
        }
    }
}
