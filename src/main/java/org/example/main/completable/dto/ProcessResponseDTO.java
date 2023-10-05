package org.example.main.completable.dto;

import java.io.Serial;
import java.io.Serializable;

public record ProcessResponseDTO(String processName, int processStatus, int value, String details) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
