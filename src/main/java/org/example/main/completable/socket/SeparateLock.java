package org.example.main.completable.socket;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Contains two locks: one for reading from socket, other for writing
 * Read and write locks are separate and do not block each other
 */
public class SeparateLock {
    private final Lock read = new ReentrantLock();
    private final Lock write = new ReentrantLock();

    public Lock read() {
        return read;
    }

    public Lock write() {
        return write;
    }
}
