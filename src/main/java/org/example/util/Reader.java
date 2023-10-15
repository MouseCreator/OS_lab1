package org.example.util;

import java.util.Scanner;

/**
 * Reader utility
 */
public class Reader {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Reads string from user
     * @param prompt - prompt value
     * @return user input
     */
    public static String readString(String prompt) {
        String inputLine;
        do {
            System.out.print(prompt);
            inputLine = scanner.nextLine();
        } while (inputLine == null || inputLine.isEmpty());
        return inputLine;
    }
    /**
     * Reads integer from user
     * @param prompt - prompt value
     * @return user input
     */
    public static Integer readInteger(String prompt) {
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
