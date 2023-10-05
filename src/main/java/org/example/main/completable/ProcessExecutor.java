package org.example.main.completable;

import java.util.concurrent.Future;

public interface ProcessExecutor<V, R> {
    void start(R input);
    V getResult();
    String getStatusDetails();
    Future.State getStatus();
}
