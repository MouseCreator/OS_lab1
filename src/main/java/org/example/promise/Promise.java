package org.example.promise;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface Promise<V> {
    V get() throws InterruptedException, ExecutionException;
    V get(long timeoutMillis) throws InterruptedException, ExecutionException, TimeoutException;
    void execute(Callable<V> callable);
    void set(V value);
    Promise<V> onComplete(Runnable r);
    Promise<V> onFail(Runnable r);
    void next();
}
