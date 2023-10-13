package org.example.util;

import java.util.Scanner;

public class Reader {
    private static final Scanner scanner = new Scanner(System.in);
    public static String read(String prompt) {
        String inputLine;
        do {
            System.out.print(prompt);
            inputLine = scanner.nextLine();
        } while (inputLine == null || inputLine.isEmpty());
        return inputLine;
    }

    public static Integer getInteger(String prompt) {
        String inputLine;
        while (true){
            System.out.print(prompt);
            inputLine = scanner.nextLine();
            if (inputLine == null || inputLine.isEmpty())
                continue;
            try {
                return Integer.parseInt(inputLine);
            } catch (Exception e) {
                continue;
            }
        }
    }
}
