package org.example.main.completable.advanced;

import org.example.main.completable.console.ConsoleManager;
import org.example.main.completable.creator.ProcessCreator;
import org.example.main.completable.creator.ProcessCreatorImpl;
import org.example.main.completable.socket.LongTermSocketManager;
import org.example.main.completable.socket.SocketManager;
import org.example.memoization.MemoizationMap;
import org.example.util.Parser;

import java.util.Optional;

/**
 * Class for communication with user
 * All the execution logic is passed to the service
 * @see org.example.main.completable.advanced.AdvancedService
 */
public class AdvancedController {
    private AdvancedService service;
    private long timeout = 4000L;
    private boolean running = true;
    private final ConsoleManager consoleManager = new ConsoleManager();

    /**
     * Starts processes and socket
     * Starts main loop
     */
    public void start() {
        try(ProcessCreator processCreator = new ProcessCreatorImpl()) {
            Process processF = processCreator.startFProcess();
            Process processG = processCreator.startGProcess();
            startSocket(processF, processG);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * starts socket
     * @param p1 - process F
     * @param p2 - process G
     */
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
        MemoizationMap<Integer, String> memoizationMap = new MemoizationMap<>();
        service = new AdvancedService(socketManager, memoizationMap, consoleManager);
    }

    /**
     * Main UI loop
     */
    private void doCalculateCycle() {
        help();
        System.out.print("Press 'Enter' to begin ===>");
        while (running) {
            consoleManager.beginInput();
            String input = consoleManager.getString();
            if (input.isEmpty()) {
                consoleManager.endInput();
                continue;
            }
            consoleManager.endInput();
            execute(input);
        }
    }

    /**
     * Executes user input
     * Splits input expression "command param1 param2 ..." to command string and array of parameters
     * @param expression - user input to be executed
     */
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
        validateAndExecute(command, params);
    }


    /**
     * Allows user to type x value without "f" prefix
     * @param expression - user input
     * @return  if input starts with decimal number, adds "f" at the beginning, otherwise returns {@param expression}
     */
    private String toCommand(String expression) {
        String[] s = expression.split("[\\s\\t]+", 2);
        Optional<Integer> integer = Parser.toInteger(s[0]);
        if (integer.isPresent()) {
            return "f " + expression;
        } else {
            return expression;
        }
    }
    /**
     * Validates the command and executes it
     * @param command - command to be executed
     * @param params - command parameters
     */
    private void validateAndExecute(String command, String[] params) {
        if(validate(command, params)) {
            response(command, params);
        } else {
            System.out.println("Command " + command + " does not take given number of parameters");
        }
    }
    /**
     * Executes command with parameters. Number of parameters is expected to be validated first
     * @param command - command to be executed
     * @param params - array of parameters
     */
    private void response(String command, String[] params) {
        switch (command) {
            case "func", "f" -> calculateValue(params);
            case "status", "s" -> status(params);
            case "exit", "e" -> close();
            case "cancel", "c" -> service.cancel();
            case "timeout", "t" -> changeTimeout(params);
            case "help", "h" -> help();
            case "delete", "d" -> deleteFromMap(params);
            default -> handleUnknown(command);
        }
    }

    private void deleteFromMap(String[] params) {
        if (params.length == 0) {
            service.clearMap();
        } else {
            for (String p : params) {
                Optional<Integer> x = Parser.toInteger(p);
                if (x.isEmpty()) {
                    System.out.println(p + " is not an integer");
                    continue;
                }
                service.clear(x.get());
            }
        }
    }

    /**
     *
     * @param command - command to validate
     * @param parameters - array of parameters
     * @return true if command can take specified number of parameters
     */
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
            case "delete", "d" -> 1;
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

    /**
     * Prints information about commands and parameters
     */
    private void help() {
        String helpString = """
                <=== HELP ===>
                
                There program has modes: Input and Output mode
                Initially program is in Output mode
                
                In Output mode, the program will be able to print to the console
                In Input mode, user is expected to provide input and all outputs will be queued
                Press 'Enter' to switch to the Input mode
                
                Expected input format is: command param1 param2 ...
                Functions are not case-sensitive
                Extra spaces and tabulations will be ignored
                
                Available commands:
                help (shortcut: h) - print help information
                func (f) [integer...] - calculate function at given arguments
                    Examples:
                    f 0 =>  calculate f(0)
                    f 0 1 => calculate f(0) and f(1) asynchronously
                    f =>    error, invalid number of parameters
                status (s) [integer...] - get calculation status
                    Examples:
                    s   =>  print statuses af all current calculations
                    s 0 =>  print status of calculation with argument 0
                            if it was calculated earlier, the time it was finished will be printed
                            if it was never calculated, program will print "Never calculated"
                exit (e) - exit the program
                cancel (c) - cancel all calculations
                timeout (t) [integer] - change timeout, default value is 4000 ms
                delete (d) [integer...] - delete values from memoization map
                    Examples:
                    d 0 => deletes f(0) if present
                    d => clears memoization map
                
                If input starts with a decimal number, it will be transformed to "f [input]"
                    Example: 0 1 => calculate f(0) and f(1)
                    
                """;
        consoleManager.print(helpString);
    }
    private void changeTimeout(String[] params) {
        assert params.length == 1;
        Optional<Long> t = Parser.toLong(params[0]);
        if (t.isEmpty()) {
            System.out.println(params[0] + " cannot be cast to a long value!");
            return;
        }
        long nt = t.get();
        if (nt > 0) {
            timeout = nt;
            consoleManager.print("Successfully changed timeout to " + timeout + "ms");
        }
        else {
            consoleManager.print("Timeout cannot be a negative value");
        }

    }

    private void close() {
        service.close();
        running = false;
    }

    private void handleUnknown(String command) {
        consoleManager.print("Unknown function:" + command);
    }

    private void status(String[] params) {
        if (params.length == 0) {
            service.statusAll();
        } else {
            for (String p : params) {
                Optional<Integer> x = Parser.toInteger(p);
                if (x.isEmpty()) {
                    consoleManager.print(p + " is not an integer");
                    continue;
                }
                service.status(x.get());
            }
        }
    }

    private void calculateValue(String[] params) {
        for (String p : params) {
            Optional<Integer> x = Parser.toInteger(p);
            if (x.isEmpty()) {
                System.out.println(p + " is not an integer");
                continue;
            }
            service.calculate(x.get(), timeout);
        }
    }

    private String toStandardForm(String expression) {
        return expression.trim().toLowerCase();
    }



}
