package org.example.main.completable;

import java.util.concurrent.Future;

public class CompletableProcessExecutor<V, R> implements ProcessExecutor<V, R> {
    @Override
    public void start(R input) {

    }

    @Override
    public V getResult() {
        return null;
    }

    @Override
    public String getStatusDetails() {
        return null;
    }

    @Override
    public Future.State getStatus() {
        return null;
    }
}
