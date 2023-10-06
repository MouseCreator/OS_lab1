package org.example.main.completable.socket;

import org.example.main.completable.calculation.CalculationParameters;
import org.example.main.completable.dto.ProcessResponseDTO;

import java.util.concurrent.LinkedBlockingQueue;

public interface SocketManager extends AutoCloseable {
    void start();
    void doServerCycle();
    void close();
    void set(CalculationParameters calculationParameters);
    LinkedBlockingQueue<ProcessResponseDTO> getResultQueue();

}
