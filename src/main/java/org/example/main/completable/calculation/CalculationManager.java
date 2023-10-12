package org.example.main.completable.calculation;

import org.example.main.completable.socket.SocketManager;
import org.example.memoization.MemoizationMap;

import java.util.concurrent.CompletableFuture;

public class CalculationManager {
    private final SocketManager socketManager;

    private final MemoizationMap<Integer> memoizationMap = new MemoizationMap<>();

    public CalculationManager(SocketManager socketManager) {
        this.socketManager = socketManager;
    }

    public CompletableFuture<String> calculateAsync(CalculationParameters parameters) {
        return CompletableFuture.supplyAsync(()-> {
            socketManager.send(parameters);
            return "";
        });

    }
}
