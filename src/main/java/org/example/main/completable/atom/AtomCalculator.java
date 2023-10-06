package org.example.main.completable.atom;

import org.example.main.completable.calculation.CalculationParameters;
import org.example.main.completable.socket.SocketManagerAtom;
import org.example.memoization.MemoizationMap;
import org.example.util.MathUtil;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AtomCalculator {

    private final SocketManagerAtom socketManager;
    private final MathUtil mathUtil = new MathUtil();
    private final MemoizationMap<Integer> memoizationMap = new MemoizationMap<>();

    public AtomCalculator(SocketManagerAtom socketManager) {
        this.socketManager = socketManager;
    }
    public String calculate(CalculationParameters calculationParameters) {
        int x = calculationParameters.x();
        Optional<String> optionalResult = memoizationMap.get(x);
        if (optionalResult.isPresent()) {
            return optionalResult.get();
        }
        CompletableFuture<Integer> futureF = socketManager.calculateF(calculationParameters);
        CompletableFuture<Integer> futureG = socketManager.calculateG(calculationParameters);
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
