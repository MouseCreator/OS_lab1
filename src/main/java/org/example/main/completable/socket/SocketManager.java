package org.example.main.completable.socket;

import org.example.main.completable.calculation.CalculationParameters;
import org.example.main.completable.dto.FunctionOutput;


public interface SocketManager extends AutoCloseable {
    void start();
    void send(CalculationParameters parameters);
    void close();
    FunctionOutput receiveF(CalculationParameters calculationParameters);
    FunctionOutput receiveG(CalculationParameters calculationParameters);

}
