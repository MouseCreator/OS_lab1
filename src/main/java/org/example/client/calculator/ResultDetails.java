package org.example.client.calculator;

import java.time.LocalDateTime;

/**
 * Contains result of executor calculation
 */
public class ResultDetails {
    private int result;
    private boolean completedExceptionally;
    private boolean hasResult;
    private LocalDateTime timeCompleted;
    private Long timeStartedMillis;

    public ResultDetails() {
        result = 0;
        hasResult = false;
        completedExceptionally = false;
        timeCompleted = null;
        timeStartedMillis = System.currentTimeMillis();
    }

    public Long getTimeStartedMillis() {
        return timeStartedMillis;
    }

    /**
     * @return calculation result f(x)
     */
    public int getResult() {
        return result;
    }

    /**
     * @param result - result of calculation f(x)
     */
    public void setResult(int result) {
        this.result = result;
    }

    /**
     *
     * @return true if calculation is done
     */
    public boolean hasResult() {
        return hasResult;
    }

    /**
     *
     * @return calculation status
     */
    public String getDetails() {
        if (hasResult) {
            return (completedExceptionally) ?
                 "Calculation was completed with critical error. Time completed: " + timeCompleted :
                 "Calculation was completed with result " + result + "Time finished " + timeCompleted;
        } else {
            return "Calculation is not finished yet";
        }
    }

    /**
     * Finishes calculation with {@param result}
     */
    public void complete(int result) {
        timeCompleted = LocalDateTime.now();
        this.result = result;
        hasResult = true;
    }

    /**
     * Finishes calculation exceptionally
     */
    public void completedExceptionally() {
        completedExceptionally = true;
        hasResult = true;
        timeCompleted = LocalDateTime.now();
    }

    /**
     * Starts timer to measure time of calculation
     */
    public void start() {
        timeStartedMillis = System.currentTimeMillis();
    }
}
