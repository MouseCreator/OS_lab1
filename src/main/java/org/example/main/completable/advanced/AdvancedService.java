package org.example.main.completable.advanced;

import org.example.main.completable.calculation.CalculationParameters;
import org.example.main.completable.dto.FunctionOutput;
import org.example.main.completable.dto.Signal;
import org.example.main.completable.socket.SocketManager;
import org.example.memoization.MemoizationMap;
import org.example.util.MathUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AdvancedService {
    private final MemoizationMap<Integer, Integer> memoizationMap;

    private final SocketManager socketManager;

    public AdvancedService(SocketManager socketManager, MemoizationMap<Integer, Integer> memoizationMap) {
        this.socketManager = socketManager;
        this.memoizationMap = memoizationMap;
    }

    public void calculate(int x, long timeout) {
        Thread calculatingThread = new Thread(() -> calculateAsync(x, timeout));
        calculatingThread.start();
    }

    private void calculateAsync(int x, long timeout) {
        CompletableFuture<FunctionOutput> futureF = socketManager.calculateF(new CalculationParameters(x, timeout, Signal.CONTINUE));
        CompletableFuture<FunctionOutput> futureG = socketManager.calculateG(new CalculationParameters(x, timeout, Signal.CONTINUE));
        try {
            MathUtil mathUtil = new MathUtil();
            FunctionOutput outputF = futureF.get();
            FunctionOutput outputG = futureG.get();

            if (outputF.processStatus() == 0 && outputG.processStatus() == 0) {
                int result = mathUtil.gcd(outputG.value(), outputF.value());
                memoizationMap.put(x,result);
                System.out.println("Result(" + x + "): " + result);
            } else {
                String s = "Error! Cannot calculate function at " + x;
                s += ("\nProcess F " + outputF.details());
                s += ("\nProcess G " + outputG.details());
                System.out.println(s);
            }
        } catch (InterruptedException e) {
            futureF.cancel(true);
            futureG.cancel(true);
            System.out.println("Calculation is interrupted!");
        } catch (ExecutionException e) {
            System.out.println("Execution error!");
            e.printStackTrace();
        }
    }

    public void close() {
        socketManager.shutdownF();
        socketManager.shutdownG();
    }

    public void cancel() {
        socketManager.cancelF();
        socketManager.cancelG();
    }

    public void statusAll() {
        String status1 = socketManager.statusF();
        String status2 = socketManager.statusG();
        System.out.println(status1 + "\n" + status2);
    }

    public void status(int x) {
        String status1 = socketManager.statusF(x);
        String status2 = socketManager.statusG(x);
        System.out.println(status1 + "\n" + status2);
    }
}
