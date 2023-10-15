package org.example.client.socket;

/**
 * Extended client socket manager
 * Creates only one socket for the lifespan of the process
 */
public interface LongTermClientSocketIO extends ClientSocketIO {
    /**
     * Open connection
     */
    void connect();

    /**
     * Closes connection
     */
    void close();
}
