package org.example.main.completable.atom;

import org.example.main.completable.calculation.CalculationParameters;
import org.example.main.completable.dto.Signal;
import org.example.main.completable.socket.LongTermSocketManager;

public class AtomController {
    private AtomCalculator atomCalculator;

    public void start() {
        if (atomCalculator != null) {
            throw new IllegalStateException("Main controller is already running");
        }
        try {
            AtomProcessManager atomProcessManager = new AtomProcessManager();
            LongTermSocketManager serverSocket = atomProcessManager.start();

            atomCalculator = new AtomCalculator(serverSocket);

            serverSocket.start();
            serverSocket.accept();

            doMainLoop();
            atomProcessManager.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void doMainLoop() {
        String result = atomCalculator.calculate(new CalculationParameters(0, 5000L, Signal.CONTINUE));
        System.out.println(result);
        String result2 = atomCalculator.calculate(new CalculationParameters(1, 5000L, Signal.CONTINUE));
        System.out.println(result2);
        String result3 = atomCalculator.calculate(new CalculationParameters(2, 5000L, Signal.CONTINUE));
        System.out.println(result3);
        atomCalculator.calculate(new CalculationParameters(512, 1000L, Signal.SHUTDOWN));
    }
}
