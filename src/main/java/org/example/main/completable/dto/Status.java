package org.example.main.completable.dto;

/**
 * Status of function output.
 * Indicates, how function output has to be processed
 */
public class Status {
    /**
     * Calculation finished successfully
     */
    public final static int SUCCESS = 0;
    /**
     * Calculation failed
     */
    public final static int CRITICAL_ERROR = 1;
    /**
     * Calculation finished with light error
     */
    public final static int LIGHT_ERROR_LIMIT = 2;
    /**
     * Calculation was interrupted
     */
    public final static int INTERRUPT = 3;
    /**
     * Calculation failed due to timeout
     */
    public final static int TIMEOUT = 4;
    /**
     * Output contains an intermediate state of the calculation
     */
    public static final int STATUS = 5;
    /**
     * Output contains an intermediate state of all calculations
     */
    public static final int STATUS_ALL = 6;
}
