package org.example.main.completable.advanced;

import org.example.main.completable.calculation.CalculationParameters;
import org.example.main.completable.creator.ProcessCreator;
import org.example.main.completable.creator.ProcessCreatorImpl;
import org.example.main.completable.dto.FunctionOutput;
import org.example.main.completable.dto.Signal;
import org.example.main.completable.socket.LongTermSocketManager;
import org.example.main.completable.socket.SocketManager;
import org.example.util.MathUtil;
import org.example.util.Reader;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AdvancedController {
    public void start() {
        try(ProcessCreator processCreator = new ProcessCreatorImpl()) {
            processCreator.startFProcess();
            processCreator.startGProcess();

            startSocket();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void startSocket() {
        try(SocketManager socketManager = new LongTermSocketManager()) {
            socketManager.start();
            socketManager.accept();

            calculate(socketManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void calculate(SocketManager socketManager) {
        while (true) {
            String input = Reader.read("> ");
            int x;
            try {
                x = Integer.parseInt(input);
                Thread calculatingThread = new Thread(() -> calculateAsync(socketManager, x));
                calculatingThread.start();
            } catch (Exception e) {
                if (input.equals("close") || input.equals("c")) {
                    return;
                } else if (input.equals("cancel") || input.equals("d")) {
                    socketManager.cancelF();
                    socketManager.cancelG();
                } else {
                    System.out.println("Unknown command");
                    continue;
                }
            }

        }
    }

    private void cancelIfPresent(CompletableFuture<String> currentComputation) {
        if (currentComputation != null) {
            currentComputation.cancel(true);
        }
    }

    private void calculateAsync(SocketManager socketManager, int x) {
        CompletableFuture<FunctionOutput> futureF = socketManager.calculateF(new CalculationParameters(x, 4000L, Signal.CONTINUE));
        CompletableFuture<FunctionOutput> futureG = socketManager.calculateG(new CalculationParameters(x, 4000L, Signal.CONTINUE));
        try {
            MathUtil mathUtil = new MathUtil();
            FunctionOutput outputF = futureF.get();
            FunctionOutput outputG = futureG.get();

            if (outputF.processStatus() == 0 && outputG.processStatus() == 0) {
                System.out.println("Result: " + mathUtil.gcd(outputG.value(), outputF.value()));
            } else {
                String s = "Error!";
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
        }
    }
}
