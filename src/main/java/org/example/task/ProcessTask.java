package org.example.task;

public interface ProcessTask<V> {
    <R> V process(R param);
}
