package org.example.main.completable.console;

import org.example.util.Reader;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Manages console input and output
 */
public class ConsoleManager {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    public void print(Object obj) {
        try {
            lock.writeLock().lock();
            System.out.println(obj);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void beginInput() {
        Reader.awaitEnter();
        lock.readLock().lock();
    }
    public String getString() {
        return Reader.readString("> ");
    }

    public void endInput() {
        lock.readLock().unlock();
    }
}
