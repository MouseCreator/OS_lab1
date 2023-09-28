package org.example.promise;

public interface Promise<V> {
    V get() throws InterruptedException;
    void set(V value);
}
