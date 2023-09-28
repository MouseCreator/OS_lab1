package org.example.promise;

public class PromiseImpl<V> implements Promise<V> {
    V value;

    private final Object synchronizer = new Object();

    public PromiseImpl() {
        this.value = null;
    }
    public void set(V val) {
        synchronized (synchronizer) {
            value = val;
            synchronizer.notifyAll();
        }
    }
    public V get() throws InterruptedException {
        synchronized (synchronizer) {
            while (value == null) {
                synchronizer.wait();
            }
            return value;
        }
    }
}
