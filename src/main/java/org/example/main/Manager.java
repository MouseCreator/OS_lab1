package org.example.main;

import org.example.memoization.MemoizationMap;

public class Manager {
    private final ConsoleManager consoleManager = new ConsoleManager();

    private final ExecutionManager executor = new ExecutionManagerImpl(new MemoizationMap<>(), new FunctionExecutor());

    public void start() {
        doMainLoop();
    }

    private void doMainLoop() {
        while (true) {
            String command = consoleManager.askForString("X: ");
            switch (command) {
                case "exit", "e" -> {
                    return;
                }
                case "menu", "m" -> menuMode();
                default -> processInteger(command);
            }

        }
    }

    private void processInteger(String v) {
        int x = Integer.parseInt(v);
        Thread thread = new Thread(()->{
            try {
                int result = executor.execute(x, 1000L);
                consoleManager.print("Result:" + result);
            } catch (Exception e) {
                consoleManager.printError(e.getMessage());
            }
        });
        thread.start();
    }

    private void menuMode() {
        try {
            consoleManager.lock();
            while (true) {
                consoleManager.print("You are in menu mode. Leave menu mode?");
                String command = consoleManager.askForString("Answer: ");
                if (command.equals("yes") || command.equals("y"))
                    break;
            }
        } finally {
            consoleManager.unlock();
        }
    }
}
