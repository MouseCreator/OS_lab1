package org.example.promise;

import java.util.concurrent.Callable;

public class ExecutorImpl<V> implements Executor<V> {
    @Override
    public Promise<V> execute(Callable<V> callable) {
        Promise<V> promise = new PromiseImpl<>();
        Thread thread = new Thread(() -> {
            try {
                V val = callable.call();
                promise.set(val);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        return promise;
    }
}
