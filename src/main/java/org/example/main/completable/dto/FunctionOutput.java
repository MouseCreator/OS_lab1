package org.example.main.completable.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * Data transfer object to be passed from client (F and G) to server
 * @param name - name of the sender
 * @param origin - value of x
 * @param processStatus - status of the calculation (success/failed)
 * @see Status
 * @param value - value of f(x) or g(x)
 * @param details - details string (error cause, number of light errors, etc.)
 */
public record FunctionOutput (String name, int origin, int processStatus, int value, String details) implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;
}
