package org.example.main.completable.dto;

import java.io.Serial;
import java.io.Serializable;

public record FunctionOutput (String name, int origin, int processStatus, int value, String details) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
