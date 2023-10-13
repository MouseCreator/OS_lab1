package org.example.main.completable.controller;

import org.example.main.completable.calculation.CalculationMain;
import org.example.main.completable.creator.ProcessCreator;
import org.example.main.completable.creator.ProcessCreatorImpl;
import org.example.main.completable.socket.LongTermSocketManager;
import org.example.main.completable.socket.SocketManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    private static SocketManager initialize() {
        SocketManager socketManager = new LongTermSocketManager();
        ProcessCreator processCreator = new ProcessCreatorImpl();
        processCreator.startFProcess();
        Process processG = processCreator.startGProcess();
        Process processF = processCreator.startFProcess();
        new Thread(()->{
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(processG.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(()->{
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(processF.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        socketManager.start();
        socketManager.accept();
        return socketManager;
    }
    public static void main(String[] args) {
        SocketManager socketManager = initialize();
        CalculationMain calculationMain = new CalculationMain(socketManager);
        Controller controller = new Controller(calculationMain);
        controller.start();
        socketManager.close();
    }

}
