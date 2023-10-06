package org.example.main.completable.calculation;

import org.example.main.completable.dto.ProcessResponseDTO;
import org.example.main.completable.socket.SocketManager;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

public class CalculationManager {
    private final SocketManager socketManager;
    public CalculationManager(SocketManager socketManager) {
        this.socketManager = socketManager;
    }

    public CompletableFuture<Integer> calculateAndGet(String processName, CalculationParameters calculationParameters) {
        socketManager.set(calculationParameters);
       return calc(calculationParameters.x(), processName);
    }

   private CompletableFuture<Integer> calc(int x, String processName) {
        return CompletableFuture.supplyAsync(()->{
            LinkedBlockingQueue<ProcessResponseDTO> resultQueue = socketManager.getResultQueue();
            while (resultQueue.peek() != null) {
                ProcessResponseDTO result = resultQueue.peek();
                if (result.origin() == x && result.processName().equals(processName)) {
                    resultQueue.poll();
                    if (result.processStatus()==0) {
                        return result.value();
                    } else {
                        throw new RuntimeException("Calculation failed: " + result.details());
                    }
                }
            }
            throw new RuntimeException(processName + ": Queue is empty! Cannot collect result");
        });
    }
}
