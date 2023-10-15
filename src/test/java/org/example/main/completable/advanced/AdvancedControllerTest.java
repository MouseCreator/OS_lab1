package org.example.main.completable.advanced;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class AdvancedControllerTest {

    private final static boolean RUN = true;
    @Test
    void testUserInput() throws IOException {
        if(!RUN)
            return;
        Scanner scanner = new Scanner(System.in);
        String input;
        while (true) {
            System.out.print("> ");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (System.in.available() > 0) {
                input = scanner.nextLine();
                if (input.equals("exit")) {
                    System.out.println("Exiting program. Goodbye!");
                    break;
                }
                System.out.print("You typed: " + input);
                scanner.nextLine();
            } else {
                System.out.println("\rHello, world");
            }
        }
    }
}