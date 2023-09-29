package org.example.promise;

import java.util.concurrent.ExecutionException;

public interface Promise<V> {
    V get() throws InterruptedException, ExecutionException;
    void set(V value);
}
