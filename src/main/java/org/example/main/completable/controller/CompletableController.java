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

        try {
            MainProcessManager mainProcessManager = new MainProcessManager();
            SocketManager serverSocket = mainProcessManager.start();
            mainCalculator = new MainCalculator(serverSocket);
            serverSocket.start();
            Thread thread = new Thread(serverSocket::doServerCycle);
            thread.start();
            doMainLoop();
            thread.interrupt();
            mainProcessManager.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void doMainLoop() {
        String result = mainCalculator.calculate(new CalculationParameters(64, 1000L, 10));
        System.out.println(result);
        String result2 = mainCalculator.calculate(new CalculationParameters(128, 1000L, 10));
        System.out.println(result2);
        String result3 = mainCalculator.calculate(new CalculationParameters(512, 1000L, 10));
        System.out.println(result3);
    }
}
