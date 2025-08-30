package meep.ui;

import java.util.Scanner;

/**
 * Simple console I/O helper for Meep.
 */
public class Ui {
    static Scanner scanner = new Scanner(System.in);

    /**
     * Reads a single line command from standard input.
     * @return the raw command line
     */
    public static String readCommand() {
        String command = scanner.nextLine();
        return command;
    }

    /**
     * Prints a response message framed by a horizontal rule.
     * @param response content to print
     */
    public static void printResponse(String response) {
        System.out.println("-".repeat(50));
        System.out.println(response);
        System.out.println("-".repeat(50));
    }
}