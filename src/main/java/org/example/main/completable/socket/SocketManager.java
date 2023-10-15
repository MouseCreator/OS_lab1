package org.example.main.completable.socket;

import org.example.main.completable.calculation.CalculationParameters;
import org.example.main.completable.dto.FunctionOutput;

import java.util.concurrent.CompletableFuture;


public interface SocketManager extends AutoCloseable {
    void start();
    void accept();
    void close();
    CompletableFuture<FunctionOutput> calculateF(CalculationParameters params);
    CompletableFuture<FunctionOutput> calculateG(CalculationParameters params);
    void cancelF();
    void cancelG();
    void shutdownF();
    void shutdownG();
    String statusF();
    String statusG();
    String statusF(int x);
    String statusG(int x);
}
