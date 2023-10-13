package org.example.main.completable.calculation;

import org.example.main.completable.dto.FunctionOutput;
import org.example.main.completable.socket.SocketManager;
import org.example.memoization.MemoizationMap;
import org.example.util.MathUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CalculationManager {
    private final SocketManager socketManager;
    private final MathUtil mathUtil = new MathUtil();
    private final MemoizationMap<Integer, Integer> memoizationMap = new MemoizationMap<>();
    public CalculationManager(SocketManager socketManager) {
        this.socketManager = socketManager;
    }
    public CompletableFuture<String> calculateAsync(CalculationParameters parameters) {
        CompletableFuture<FunctionOutput> futureF = socketManager.calculateF(parameters);
        CompletableFuture<FunctionOutput> futureG = socketManager.calculateG(parameters);
        return CompletableFuture.supplyAsync(()->{
            try {
                String result = calculate(parameters, futureF, futureG);
                System.out.println(result);
                return result;
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String calculate(CalculationParameters parameters,
                             CompletableFuture<FunctionOutput> futureF,
                             CompletableFuture<FunctionOutput> futureG) throws InterruptedException, ExecutionException {
        FunctionOutput FOutput = futureF.get();
        FunctionOutput GOutput = futureG.get();
        if (FOutput.processStatus() == 0 && GOutput.processStatus() == 0) {
            int calculationResult = mathUtil.gcd(FOutput.value(), GOutput.value());
            memoizationMap.put(parameters.x(), calculationResult);
            return "Result: " + calculationResult;
        } else {
            String result = "Computation failed:\nF status: ";
            result += FOutput.details();
            result += "\nG status: ";
            result += GOutput.details();
            return result;
        }
    }

}
