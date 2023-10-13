package org.example.main.completable.calculation;

import org.example.main.completable.socket.SocketManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CalculationMain {
    private final CalculationManager calculationManager;
    private List<CompletableFuture<String>> results = null;
    public CalculationMain(SocketManager socketManager) {
        calculationManager = new CalculationManager(socketManager);
    }
    public synchronized void calculate(CalculationParameters[] parametersArray) {
        if (results != null) {
            throw new IllegalStateException("Other calculation in progress");
        }
        int N = parametersArray.length;
        results = new ArrayList<>(N);
        for (CalculationParameters parameters : parametersArray) {
            results.add(calculationManager.calculateAsync(parameters));
        }
        for (int i = 0; i < N; i++) {
            try {
                System.out.println(results.get(i).get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        results = null;
    }
    public synchronized String status(){
       if (results == null) {
           throw new IllegalStateException("No calculation in progress");
       }
       StringBuilder builder = new StringBuilder();
       for (CompletableFuture<String> result : results) {
           builder.append(result.state().name()).append('\n');
       }
       return builder.toString();
    }

    public synchronized void cancel(){
        if (results == null) {
            throw new IllegalStateException("No calculation in progress");
        }
        for (CompletableFuture<String> result : results) {
            result.cancel(true);
        }
    }
}
