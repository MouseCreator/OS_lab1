package org.example.main.completable.dto;

/**
 * Represents a signal to client, how to process input value
 * For instance, x = 0 with Signal CONTINUE means to calculate f(0), but with Signal STATUS means to get status of f(0)
 */
public class Signal {
    /**
     * calculate function
     */
    public final static int CONTINUE = 0;
    /**
     * cancel calculation
     */
    public final static int RESTART = 1;
    /**
     * cancel all calculations and close
     */
    public final static int SHUTDOWN = 2;
    /**
     * get status of calculation
     */
    public final static int STATUS = 3;
    /**
     * get status of all calculations
     */
    public final static int STATUS_ALL = 4;
}
