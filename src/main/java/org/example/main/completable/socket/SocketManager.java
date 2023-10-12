package org.example.main.completable.socket;

import org.example.main.completable.calculation.CalculationParameters;


public interface SocketManager extends AutoCloseable {
    void start();
    void doServerCycle();
    void close();
    void set(CalculationParameters calculationParameters);

}
