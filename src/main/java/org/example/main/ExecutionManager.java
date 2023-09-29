package org.example.main;

public interface ExecutionManager {
    int execute(int x, long timeout) throws Exception;
    void close();
    void start();
}
