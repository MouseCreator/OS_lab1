package org.example.main.completable.creator;

public interface ProcessCreator extends AutoCloseable {
    Process startFProcess();
    Process startGProcess();
}
