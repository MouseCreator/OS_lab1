package org.example.main.completable.atom;

import org.example.main.completable.calculation.CalculationParameters;
import org.example.main.completable.socket.SocketManagerAtom;

public class AtomController {
    private AtomCalculator atomCalculator;

    public void start() {
        if (atomCalculator != null) {
            throw new IllegalStateException("Main controller is already running");
        }
        try {
            AtomProcessManager atomProcessManager = new AtomProcessManager();
            SocketManagerAtom serverSocket = atomProcessManager.start();

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
        String result = atomCalculator.calculate(new CalculationParameters(64, 1000L, 10));
        System.out.println(result);
        String result2 = atomCalculator.calculate(new CalculationParameters(128, 1000L, 10));
        System.out.println(result2);
        String result3 = atomCalculator.calculate(new CalculationParameters(512, 1000L, 10));
        System.out.println(result3);
    }
}
