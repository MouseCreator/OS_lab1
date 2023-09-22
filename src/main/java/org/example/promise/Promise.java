package org.example.promise;

public interface Promise<V> {
    V get();
    void set(V value);
}
