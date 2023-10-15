package org.example.main.completable.advanced;

import org.example.main.completable.calculation.CalculationParameters;
import org.example.main.completable.creator.ProcessCreator;
import org.example.main.completable.creator.ProcessCreatorImpl;
import org.example.main.completable.dto.FunctionOutput;
import org.example.main.completable.dto.Signal;
import org.example.main.completable.socket.LongTermSocketManager;
import org.example.main.completable.socket.SocketManager;
import org.example.memoization.MemoizationMap;
import org.example.util.MathUtil;
import org.example.util.Reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AdvancedController {

    private final MemoizationMap<Integer, Integer> memoizationMap = new MemoizationMap<>();
    public void start() {
        try(ProcessCreator processCreator = new ProcessCreatorImpl()) {
            Process processF = processCreator.startFProcess();
            Process processG = processCreator.startGProcess();
            initListener(processG);
            initListener(processF);
            startSocket(processF, processG);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initListener(Process process) {
        Thread t = new Thread(()->{
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        t.setDaemon(true);
        t.start();
    }

    private void startSocket(Process p1, Process p2) {
        try(SocketManager socketManager = new LongTermSocketManager()) {
            socketManager.start();
            socketManager.accept();
            calculate(socketManager);
            p1.waitFor();
            p2.waitFor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void calculate(SocketManager socketManager) {
        while (true) {
            String input = Reader.readString("> ");
            int x;
            try {
                x = Integer.parseInt(input);
                if (memoizationMap.isComputed(x)) {
                    Optional<Integer> result = memoizationMap.get(x);
                    assert result.isPresent();
                    System.out.println("From memoization map: F(" + x + ") = " + result.get());
                    return;
                }
                Thread calculatingThread = new Thread(() -> calculateAsync(socketManager, x));
                calculatingThread.start();
            } catch (Exception e) {
                switch (input) {
                    case "close", "c" -> {
                        socketManager.shutdownF();
                        socketManager.shutdownG();
                        return;
                    }
                    case "cancel", "d" -> {
                        socketManager.cancelF();
                        socketManager.cancelG();
                    }
                    case "status", "s" -> {
                        String status1 = socketManager.statusF();
                        String status2 = socketManager.statusG();
                        System.out.println(status1 + "\n" + status2);
                    }
                    default -> {
                        System.out.println("Unknown command");
                        continue;
                    }
                }
            }

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
}
