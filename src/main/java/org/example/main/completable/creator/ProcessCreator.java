package org.example.main.completable.creator;

/**
 * Creates process F and G
 */
public interface ProcessCreator extends AutoCloseable {
    /**
     * Builds and starts process F
     * @return process F
     */
    Process startFProcess();

    /**
     *  Builds and starts process G
     * @return process G
     */
    Process startGProcess();
}
