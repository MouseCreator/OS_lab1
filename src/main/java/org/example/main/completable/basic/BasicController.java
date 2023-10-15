package org.example.main.completable.basic;

import org.example.main.completable.dto.CalculationParameters;
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

/**
 * Calculates only once
 * No commands like 'status' or 'cancel' expected
 * The safest and simplest implementation
 */
public class BasicController {
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
        int x = Reader.readInteger("x = ");
        CompletableFuture<FunctionOutput> futureF = socketManager.calculateF(new CalculationParameters(x, 40000L, Signal.CONTINUE));
        CompletableFuture<FunctionOutput> futureG = socketManager.calculateG(new CalculationParameters(x, 40000L, Signal.CONTINUE));
        try {
            MathUtil mathUtil = new MathUtil();
            FunctionOutput outputF = futureF.get();
            FunctionOutput outputG = futureG.get();

            if (outputF.processStatus() == 0 && outputG.processStatus() == 0) {
                System.out.println("Result: " + mathUtil.gcd(outputG.value(),outputF.value()));
            } else {
                System.out.println("Error!");
                System.out.println("Process F " + outputF.details());
                System.out.println("Process G " + outputG.details());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
