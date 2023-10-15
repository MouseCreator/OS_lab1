package org.example.main.completable.dto;

/**
 * Function input, used on the server side
 * @param x - value of X
 * @param timeout - calculation timeout
 * @param signal - signal
 * @see Signal
 */
public record CalculationParameters(int x, long timeout, int signal) {
}
