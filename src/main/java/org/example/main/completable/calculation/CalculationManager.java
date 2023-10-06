package org.example.main.completable.calculation;

import org.example.main.completable.dto.ProcessResponseDTO;
import org.example.main.completable.socket.SocketManager;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

public class CalculationManager {
    public CalculationManager(SocketManager socketManager) {
        resultQueue = socketManager.getResultQueue();
    }

    public CompletableFuture<Integer> calculateAndGet(String processName, CalculationParameters calculationParameters) {
        return calc(calculationParameters.x(), processName);
    }
    private final LinkedBlockingQueue<ProcessResponseDTO> resultQueue;
   private CompletableFuture<Integer> calc(int x, String processName) {
        return CompletableFuture.supplyAsync(()->{
            while (true) {
                ProcessResponseDTO result = resultQueue.peek();
                synchronized (resultQueue) {
                    while (result == null) {
                        try {
                            resultQueue.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        result = resultQueue.peek();
                    }
                }
                if (result.origin() == x && result.processName().equals(processName)) {
                    resultQueue.poll();
                    if (result.processStatus()==0) {
                        return result.value();
                    } else {
                        throw new RuntimeException("Calculation failed: " + result.details());
                    }
                }
            }
            //throw new RuntimeException(processName + ": Queue is empty! Cannot collect result");
        });
    }
}
