package org.example.main.completable.calculation;

import org.example.main.completable.socket.SocketManager;
import org.example.memoization.MemoizationMap;
import org.example.util.MathUtil;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainCalculator {
    private final CalculationManager calculationManager;
    private final SocketManager socketManager;

    private final MathUtil mathUtil = new MathUtil();
    private final MemoizationMap<Integer> memoizationMap = new MemoizationMap<>();
    public MainCalculator(SocketManager socketManager) {
        this.socketManager = socketManager;
        this.calculationManager = new CalculationManager(socketManager);
    }
    public String calculate(CalculationParameters calculationParameters) {
        int x = calculationParameters.x();
        Optional<String> optionalResult = memoizationMap.get(x);
        if(optionalResult.isPresent()) {
            return optionalResult.get();
        }
        socketManager.set(calculationParameters);
        CompletableFuture<Integer> futureF = calculationManager.calculateAndGet("Process F", calculationParameters);
        CompletableFuture<Integer> futureG = calculationManager.calculateAndGet("Process G", calculationParameters);
        try {
            Integer fx = futureF.get();
            Integer gx = futureG.get();
            int result = mathUtil.gcd(fx, gx);
            String successMessage = "Process finished successfully with result: " + result;
            memoizationMap.put(x, successMessage);
            return successMessage;
        } catch (ExecutionException e) {
            String failureMessage = e.getCause().getMessage();
            memoizationMap.put(x, "Process failed: " + failureMessage);
            return failureMessage;
        } catch (InterruptedException e) {
            return "Calculation was interrupted";
        }
    }
}
