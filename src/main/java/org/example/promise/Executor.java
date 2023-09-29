package org.example.promise;

import java.util.concurrent.Callable;

public interface Executor<V> {
    Promise<V> execute(Callable<V> callable);
}
