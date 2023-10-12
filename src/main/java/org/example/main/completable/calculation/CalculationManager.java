package org.example.main.completable.calculation;

import org.example.main.completable.dto.FunctionOutput;
import org.example.main.completable.socket.SocketManager;
import org.example.memoization.MemoizationMap;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CalculationManager {
    private final SocketManager socketManager;
    private final BlockingQueue<FunctionOutput> outputBlockingQueue = new LinkedBlockingQueue<>();
    private final MemoizationMap<Integer> memoizationMap = new MemoizationMap<>();

    public CalculationManager(SocketManager socketManager) {
        this.socketManager = socketManager;
    }

}
