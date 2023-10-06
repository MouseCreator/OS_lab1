package org.example.main.completable.dto;

import java.io.Serial;
import java.io.Serializable;

public record ProcessRequestDTO(int value, long timeout, int limitAttempts) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
