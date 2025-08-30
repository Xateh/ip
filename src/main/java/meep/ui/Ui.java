package meep.ui;

import java.util.Scanner;

/**
 * Handles user input and output for the Meep CLI application.
 */
public class Ui {
    static Scanner scanner = new Scanner(System.in);

     /**
      * Reads the next line entered by the user from standard input.
      *
      * @return the raw command string entered by the user
      */
    public static String readCommand() {
        String command = scanner.nextLine();
        return command;
    }

     /**
      * Prints a response message wrapped with a simple horizontal divider.
      *
      * @param response the message to display to the user
      */
    public static void printResponse(String response) {
        System.out.println("-".repeat(50));
        System.out.println(response);
        System.out.println("-".repeat(50));
    }
}