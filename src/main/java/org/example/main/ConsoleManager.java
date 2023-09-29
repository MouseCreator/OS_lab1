package org.example.main;

import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class ConsoleManager {

    private static final ReentrantLock lock = new ReentrantLock();

    public void print(String message) {
        System.out.println(message);
    }

    public String askForString(String prompt) {
        Scanner scanner = new Scanner(System.in);
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine();
        } while (input == null);

        return input;
    }

    public void printError(String message) {
        System.err.println(message);
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

}
