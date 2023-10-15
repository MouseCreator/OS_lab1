package org.example.client.calculator;

import java.time.LocalDateTime;

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

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public boolean hasResult() {
        return hasResult;
    }

    public String getDetails() {
        if (hasResult) {
            return (completedExceptionally) ?
                 "Calculation was completed with critical error. Time completed: " + timeCompleted :
                 "Calculation was completed with result " + result + "Time finished " + timeCompleted;
        } else {
            return "Calculation is not finished yet";
        }
    }
    public void complete(int result) {
        timeCompleted = LocalDateTime.now();
        this.result = result;
        hasResult = true;
    }
    public void completedExceptionally() {
        completedExceptionally = true;
        hasResult = true;
        timeCompleted = LocalDateTime.now();
    }

    public void start() {
        timeStartedMillis = System.currentTimeMillis();
    }
}
