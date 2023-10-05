package org.example.main.completable.dto;

import java.io.Serializable;

public class ProcessDTO implements Serializable {
    private String processName;
    private int processStatus;
    private int value;
    private String details;
    public String getProcessName() {
        return processName;
    }
    public ProcessDTO() {
    }
    public ProcessDTO(String processName, int processStatus, int processValue, String details) {
        this.processName = processName;
        this.processStatus = processStatus;
        this.value = processValue;
        this.details = details;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public int getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(int processStatus) {
        this.processStatus = processStatus;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }


}
