package org.example.main.completable.basic;

import java.util.Scanner;

public class Reader {
    public static String read(String prompt) {
        try(Scanner scanner = new Scanner(System.in)) {
            String inputLine;
            do {
                System.out.print(prompt);
                inputLine = scanner.nextLine();
            } while (inputLine == null || inputLine.isEmpty());
            return inputLine;
        }
    }
}
