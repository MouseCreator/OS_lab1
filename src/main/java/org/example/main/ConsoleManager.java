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

    public String askForString(String prompt) {
        lock();
        try {
            Scanner scanner = new Scanner(System.in);
            String input;
            do {
                System.out.print(prompt);
                input = scanner.nextLine();
            } while (input == null);

            return input;
        } finally {
            unlock();
        }
    }

    public void printError(String message) {
        lock();
        System.out.println(message);
        unlock();
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

}
