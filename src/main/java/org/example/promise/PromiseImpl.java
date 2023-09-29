package org.example.promise;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

public class PromiseImpl<V> implements Promise<V> {
    private State state = State.PROCESSING;

    public V getNext() throws ExecutionException, InterruptedException {
        next();
        return get();
    }

    enum State {
        PROCESSING, COMPLETED, FAILED
    }

    private ExecutionException exception;

    private final LinkedBlockingQueue<V> resultQueue;

    private final Object synchronizer = new Object();

    public PromiseImpl() {
        resultQueue = new LinkedBlockingQueue<>();
    }
    public void set(V val) {
        synchronized (synchronizer) {
            resultQueue.add(val);
            synchronizer.notifyAll();
        }
    }
    public V get() throws InterruptedException, ExecutionException {
        synchronized (synchronizer) {
            while (state == State.PROCESSING) {
                synchronizer.wait();
            }
            if (state == State.FAILED) {
                throw exception;
            }
            return resultQueue.peek();
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
            return resultQueue.peek();
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

    public void next() {
        throw new UnsupportedOperationException();
        //synchronized (synchronizer) {
            //resultQueue.poll();
            //state = State.PROCESSING;
        //}
    }
}
