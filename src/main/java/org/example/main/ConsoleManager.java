package org.example.main;

import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class ConsoleManager {

    private static final ReentrantLock lock = new ReentrantLock();

    public void print(String message) {
        lock();
        try {
            System.out.println(message);
        } finally {
            unlock();
        }
    }
    public int askForInt(String prompt) {
        lock();
        try {
            Scanner scanner = new Scanner(System.in);
            String input;
            int res;
            do {
                System.out.print(prompt);
                input = scanner.nextLine();
                if ((input == null || input.isEmpty()))
                    continue;
                try {
                    res = Integer.parseInt(input);
                } catch (Exception e) {
                    continue;
                }
                return res;
            } while (true);
        } finally {
            unlock();
        }
    }

    public String askForString(String prompt) {
        Scanner scanner = new Scanner(System.in);
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine();

        } while (input == null || input.isEmpty());

        return input;
    }

    public void printError(String message) {
        lock();
        System.err.println(message);
        unlock();
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

}
