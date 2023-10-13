package org.example.main.completable.controller;

import org.example.main.completable.calculation.CalculationMain;
import org.example.main.completable.socket.LongTermSocketManager;
import org.example.main.completable.socket.SocketManager;

public class Main {
    public static void main(String[] args) {
        SocketManager socketManager = new LongTermSocketManager();
        socketManager.start();
        CalculationMain calculationMain = new CalculationMain(socketManager);
        Controller controller = new Controller(calculationMain);
        controller.start();
        socketManager.close();
    }

}
