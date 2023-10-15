package org.example.main.completable.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * Data transfer object to send from server to F and G
 * @param value - value of X to be calculated
 * @param timeout - calculation timeout
 * @param signal - signal to be sent
 * @see Signal
 */
public record FunctionInput (int value, long timeout, int signal) implements Serializable {
    @Serial
    private static final long serialVersionUID = 3L;
}
