package org.example.main.completable.atom;

import org.example.main.completable.calculation.CalculationParameters;
import org.example.main.completable.dto.FunctionOutput;
import org.example.main.completable.socket.LongTermSocketManager;
import org.example.memoization.MemoizationMap;
import org.example.util.MathUtil;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AtomCalculator {

    private final LongTermSocketManager socketManager;
    private final MathUtil mathUtil = new MathUtil();
    private final MemoizationMap<Integer> memoizationMap = new MemoizationMap<>();

    public AtomCalculator(LongTermSocketManager socketManager) {
        this.socketManager = socketManager;
    }
    public String calculate(CalculationParameters calculationParameters) {
        int x = calculationParameters.x();
        Optional<String> optionalResult = memoizationMap.get(x);
        if (optionalResult.isPresent()) {
            return optionalResult.get();
        }
        CompletableFuture<FunctionOutput> futureF = socketManager.calculateF(calculationParameters);
        CompletableFuture<FunctionOutput> futureG = socketManager.calculateG(calculationParameters);
        try {
            int fx = futureF.get().value();
            int gx = futureG.get().value();
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
