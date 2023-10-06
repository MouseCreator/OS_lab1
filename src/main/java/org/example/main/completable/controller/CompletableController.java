package org.example.main.completable.controller;

import org.example.main.completable.calculation.CalculationParameters;
import org.example.main.completable.calculation.MainCalculator;
import org.example.main.completable.calculation.MainProcessManager;
import org.example.main.completable.socket.SocketManager;

public class CompletableController {

    private MainCalculator mainCalculator;
    public void start() {
        if (mainCalculator != null) {
            throw new IllegalStateException("Main controller is already running");
        }
        try(MainProcessManager mainProcessManager = new MainProcessManager()) {
            SocketManager serverSocket = mainProcessManager.start();
            mainCalculator = new MainCalculator(serverSocket);
            doMainLoop();
        }
    }

    private void doMainLoop() {
        String result = mainCalculator.calculate(new CalculationParameters(64, 1000L, 10));
        System.out.println(result);
    }
}
