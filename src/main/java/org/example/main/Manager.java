package org.example.main;

import org.example.memoization.MemoizationMap;

public class Manager {
    private final ConsoleManager consoleManager = new ConsoleManager();
    private final ExecutionManager executor = new ExecutionManagerImpl(new MemoizationMap<>(), new FunctionExecutor());

    public void start() {
        doMainLoop();
    }

    private void doMainLoop() {
        executor.start();
        long t = 1000L;
        try {
            while (true) {
                String command = consoleManager.askForString("X: ");
                switch (command) {
                    case "exit", "e" -> {
                        return;
                    }
                    case "menu", "m" -> menuMode();
                    case "time", "t" -> t = changeTimeout();
                    case "" -> {
                    }
                    default -> processInteger(command, t);
                }

            }
        } finally {
            executor.close();
        }
    }

    private long changeTimeout() {
        String s = consoleManager.askForString("Timeout: ");
        return Long.parseLong(s);
    }

    private void processInteger(String v, long timeout) {
        int x = Integer.parseInt(v);

        Thread thread = new Thread(()->{
            try {
                int result = executor.execute(x, timeout);
                consoleManager.print("\nResult: " + result);
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
                consoleManager.print("You are in the menu mode. Do you want to leave the menu mode?");
                String command = consoleManager.askForString("Answer: ");
                if (command.equals("yes") || command.equals("y"))
                    break;
            }
        } finally {
            consoleManager.unlock();
        }
    }
}
