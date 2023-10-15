package org.example.main.completable.advanced;

import org.example.main.completable.creator.ProcessCreator;
import org.example.main.completable.creator.ProcessCreatorImpl;
import org.example.main.completable.socket.LongTermSocketManager;
import org.example.main.completable.socket.SocketManager;
import org.example.memoization.MemoizationMap;
import org.example.util.Reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class AdvancedController {

    private AdvancedService service;
    private long timeout = 4000L;
    private boolean running = true;
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
            initService(socketManager);
            doCalculateCycle();
            p1.waitFor();
            p2.waitFor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initService(SocketManager socketManager) {
        MemoizationMap<Integer, Integer> memoizationMap = new MemoizationMap<>();
        service = new AdvancedService(socketManager, memoizationMap);
    }
    private void doCalculateCycle() {
        while (running) {
            String input = Reader.readString("> ");
            execute(input);
        }
    }

    private void execute(String expression) {
        String formatted = toStandardForm(expression);
        String withCommand = toCommand(formatted);
        String[] s = withCommand.split("[\\s\\t]+", 2);
        String command;
        String[] params;
        if (s.length == 1) {
            command = s[0];
            params = new String[0];
        } else {
            command = s[0];
            params = s[1].split("[\\s\\t]+");
        }
        if(validate(command, params)) {
            response(command, params);
        } else {
            System.out.println("Command " + command + " does not take given number of parameters");
        }
    }

    /**
     * Allows user to type x value without "f" prefix
     * @param expression - user input
     * @return  if input starts with decimal number, adds "f" at the beginning, otherwise returns {@param expression}
     */
    private String toCommand(String expression) {
        String[] s = expression.split("[\\s\\t]+", 2);
        Optional<Integer> integer = toInteger(s[0]);
        if (integer.isPresent()) {
            return "f " + expression;
        } else {
            return expression;
        }
    }

    private void response(String command, String[] params) {
        switch (command) {
            case "func", "f" -> calculateValue(params);
            case "status", "s" -> status(params);
            case "exit", "e" -> close();
            case "cancel", "c" -> service.cancel();
            case "timeout", "t" -> changeTimeout(params);
            case "help", "h" -> help();
            default -> handleUnknown(command);
        }
    }

    private boolean validate(String command, String[] parameters) {
        final int ANY = -2;
        final int AT_LEAST_ONE = -1;
        int expected = switch (command) {
            case "func", "f" -> AT_LEAST_ONE;
            case "status", "s" -> ANY;
            case "exit", "e" -> 0;
            case "cancel", "c" -> 0;
            case "help", "h" -> 0;
            case "timeout", "t" -> 1;
            default -> ANY;
        };
        if (expected == ANY) {
            return true;
        }
        if (expected == AT_LEAST_ONE) {
            return parameters.length > 0;
        }
        return parameters.length == expected;
    }

    private void changeTimeout(String[] params) {
        assert params.length == 1;
        Optional<Long> t = toLong(params[0]);
        if (t.isEmpty()) {
            return;
        }
        timeout = t.get();
    }

    private void close() {
        service.close();
        running = false;
    }

    private void help() {
        System.out.println("HELP");
    }

    private void handleUnknown(String command) {
        System.out.println("Unknown function:" + command);
    }

    private void status(String[] params) {
        if (params.length == 0) {
            service.statusAll();
        } else {
            for (String p : params) {
                Optional<Integer> x = toInteger(p);
                if (x.isEmpty()) {
                    continue;
                }
                service.status(x.get());
            }
        }
    }

    private void calculateValue(String[] params) {
        for (String p : params) {
            Optional<Integer> x = toInteger(p);
            if (x.isEmpty()) {
                continue;
            }
            service.calculate(x.get(), timeout);
        }
    }

    private Optional<Integer> toInteger(String s) {
        try {
            int value = Integer.parseInt(s);
            return Optional.of(value);
        } catch (Exception e) {
            System.out.println(s + " is not an integer!");
            return Optional.empty();
        }
    }
    private Optional<Long> toLong(String s) {
        try {
            long value = Long.parseLong(s);
            return Optional.of(value);
        } catch (Exception e) {
            System.out.println(s + " is not an integer!");
            return Optional.empty();
        }
    }

    private String toStandardForm(String expression) {
        return expression.trim().toLowerCase();
    }

}
