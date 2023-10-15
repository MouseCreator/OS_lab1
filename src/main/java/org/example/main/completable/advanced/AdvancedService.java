package org.example.main.completable.advanced;

import org.example.main.completable.console.ConsoleManager;
import org.example.main.completable.dto.CalculationParameters;
import org.example.main.completable.dto.FunctionOutput;
import org.example.main.completable.dto.Signal;
import org.example.main.completable.dto.Status;
import org.example.main.completable.socket.SocketManager;
import org.example.memoization.MemoizationMap;
import org.example.util.MathUtil;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Class that manages calculations and initiates communications via socket manager
 * @see SocketManager
 */
public class AdvancedService {
    private final MemoizationMap<Integer, String> memoizationMap;
    private final SocketManager socketManager;
    private final ConsoleManager consoleManager;

    public AdvancedService(SocketManager socketManager, MemoizationMap<Integer, String> memoizationMap, ConsoleManager consoleManager) {
        this.socketManager = socketManager;
        this.memoizationMap = memoizationMap;
        this.consoleManager = consoleManager;
    }

    public void calculate(int x, long timeout) {
        if (memoizationMap.isComputed(x)) {
            Optional<String> precalculated = memoizationMap.get(x);
            assert precalculated.isPresent();
            consoleManager.print("From memoization map\n" + precalculated.get());
            return;
        }
        calculateNew(x, timeout);
    }

    public void calculateNew(int x, long timeout) {
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

            if (outputF.processStatus() == Status.SUCCESS && outputG.processStatus() == Status.SUCCESS) {
                printSuccess(x, mathUtil, outputF, outputG);
            } else if  (outputF.processStatus() == Status.INTERRUPT || outputG.processStatus() == Status.INTERRUPT) {
                printInterrupt(x);
            }
            else {
                printError(x, outputF, outputG);
            }
        } catch (InterruptedException e) {
            futureF.cancel(true);
            futureG.cancel(true);
            consoleManager.print("Calculation is interrupted!");
        } catch (ExecutionException e) {
            consoleManager.print("Execution error!");
            e.printStackTrace();
        }
    }

    private void printError(int x, FunctionOutput outputF, FunctionOutput outputG) {
        String errorString = String.format("Result(%d) = CRITICAL ERROR", x);
        String detailedString = errorString + ". Caused by:";
        if (outputF.processStatus() != Status.SUCCESS) {
            detailedString += ("\n\tProcess F " + outputF.details());
        }
        if (outputG.processStatus() != Status.SUCCESS) {
            detailedString += ("\n\tProcess G " + outputG.details());
        }
        memoizationMap.put(x, errorString);
        consoleManager.print(detailedString);
    }

    private void printInterrupt(int x) {
        String interruptString = String.format("Result(%d) = CALCULATION INTERRUPTED", x);
        consoleManager.print(interruptString);
    }

    private void printSuccess(int x, MathUtil mathUtil, FunctionOutput outputF, FunctionOutput outputG) {
        int result = mathUtil.gcd(outputG.value(), outputF.value());
        String successString = String.format("Result(%d) = %d", x, result);
        consoleManager.print(successString);
        memoizationMap.put(x, successString);
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
        consoleManager.print(status1 + "\n" + status2);
    }

    public void status(int x) {
        String status1 = socketManager.statusF(x);
        String status2 = socketManager.statusG(x);
        consoleManager.print(status1 + "\n" + status2);
    }

    public void clearMap() {
        memoizationMap.clear();
    }

    public void clear(int at) {
        memoizationMap.remove(at);
    }
}
