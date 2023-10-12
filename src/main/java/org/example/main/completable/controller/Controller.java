package org.example.main.completable.controller;

import org.example.main.completable.calculation.CalculationParameters;
import org.example.main.completable.dto.Signal;

import java.util.Scanner;

public class Controller {
    private boolean closed = false;
    private final Scanner scanner = new Scanner(System.in);
    private long timeout = 5000L;
    public void start() {
        printHelp();
        doMainLoop();
    }
    private void doMainLoop() {
        while (!closed) {
            String expression = readString(">");
            execute(expression);
        }
    }

    private String readString(String prompt) {
        String inputLine = "";
        do {
            System.out.print(prompt);
            inputLine = scanner.nextLine();
        } while (inputLine == null || inputLine.isEmpty());
        return inputLine;
    }

    private void execute(String expression) {
        String formatted = toStandardForm(expression);
        String[] s = formatted.split("[\\s\\t]+", 2);
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
            printError("Command " + command + " does not take given number of parameters");
        }
    }

    private String toStandardForm(String expression) {
        return expression.trim().toLowerCase();
    }

    private boolean validate(String command, String[] parameters) {
        int expectedParameters = switch (command) {
            case "func", "f"  -> -1;
            case "print", "p"  -> -2;
            case "timeout", "t" -> 1;
            default -> 0;
        };
        if (expectedParameters == -1) {
            return parameters.length > 0; //takes at least one
        }
        else if (expectedParameters == -2) {
            return true; //takes any number of parameters
        }
        return parameters.length == expectedParameters;
    }
    private void response(String command, String[] parameters) {
        switch (command) {
            case "func", "f" -> calculateFunction(parameters);
            case "stop", "s" -> stopExecution();
            case "help", "h" -> printHelp();
            case "clear", "l" -> clearMap();
            case "close", "c" -> close();
            case "print", "p" -> printMap(parameters);
            case "timeout", "t" -> changeTimeout(parameters);
            case "menu", "m" -> menuMode();
            case "now", "n" -> printStatus();
            case "example", "e" -> executeExample();
            default -> printUnknown();
        }
    }

    private void printHelp() {
        String help = """
                HELP:
                \tfunc, f [integer]... - calculate function at given values. Example: f 0 1 2 3
                \tstop, s - cancel calculation
                \tclose, c - close the program
                \thelp, h - print help
                \ttimeout, t [integer] - change timeout (milliseconds)
                \tclear, l - clear memoization map
                \tprint, p [integer]... - print memoization map at given values
                \texample, e - executes "f 0 1 2 3"
                \tnow, n - print calculations statuses at the moment
                \tmenu, m - enter menu mode
                """;
        System.out.println(help);
    }

    private void changeTimeout(String[] parameters) {
        try {
            String toParse = parameters[0];
            timeout = Long.parseLong(toParse);
        } catch (Exception e) {
           printError("Cannot parse timeout value");
        }
    }

    private void close() {
        closed = true;
    }

    private void executeExample() {

    }

    private void printUnknown() {
        System.out.println("Unknown command. Print 'help' to see valid commands");
    }

    private void menuMode() {

    }

    private void printMap(String[] parameters) {

    }

    private void clearMap() {
    }

    private void stopExecution() {

    }

    private void calculateFunction(String[] parameters) {

    }

    private void calculateSingle(int x) {
        CalculationParameters calculationParameters = new CalculationParameters(x, timeout, Signal.CONTINUE);
    }

    private void printError(String message) {
        System.err.println(message);
    }

    private void printStatus() {

    }


}
