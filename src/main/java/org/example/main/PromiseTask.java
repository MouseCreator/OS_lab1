package org.example.main;

import org.example.promise.Promise;
import org.example.promise.PromiseImpl;

import java.util.concurrent.Callable;

public class PromiseTask {

    private final Callable<Integer> task;

    public PromiseTask(Callable<Integer> task) {
        this.task = task;
    }

    public Promise<Integer> awaitResult() {
        Promise<Integer> promise = new PromiseImpl<>();
        Thread thread = new Thread(() -> {
            try {
                promise.set(task.call());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        return promise;
    }
}
