package org.example.main.completable.dto;

import java.io.Serial;
import java.io.Serializable;

public record FunctionInput (int value, long timeout, int signal) implements Serializable {
    @Serial
    private static final long serialVersionUID = 3L;
}
