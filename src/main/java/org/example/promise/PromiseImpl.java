package org.example.promise;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class PromiseImpl<V> implements Promise<V> {
    private V value;
    private State state = State.PROCESSING;
    enum State {
        PROCESSING, COMPLETED, FAILED
    }

    private ExecutionException exception;

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

    public V get(long timeoutMillis) throws InterruptedException, ExecutionException, TimeoutException {
        synchronized (synchronizer) {
            if (state == State.PROCESSING)
                synchronizer.wait(timeoutMillis);
            if (state == State.PROCESSING) {
                throw new TimeoutException("Promise getter timeout");
            }
            if (state == State.FAILED) {
                throw exception;
            }
            return value;
        }
    }

    public void execute(Callable<V> callable) {
        Thread thread = new Thread(() -> {
            try {
                V val = callable.call();
                set(val);
                complete();
            } catch (Exception e) {
                exception = new ExecutionException(e);
                fail();
            }
        });
        thread.start();
    }

    private void complete() {
        state = State.COMPLETED;
        if (onComplete != null)
            onComplete.run();
    }
    private void fail() {
        state = State.FAILED;
        if (onFail != null) {
            onFail.run();
        }
    }
    private Runnable onComplete = null;
    private Runnable onFail = null;
    public Promise<V> onComplete(Runnable r) {
        onComplete = r;
        return this;
    }
    public Promise<V> onFail(Runnable r) {
        onFail = r;
        return this;
    }
}
