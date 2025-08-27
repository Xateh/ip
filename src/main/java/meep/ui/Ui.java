package meep.ui;

import java.util.Scanner;

public class Ui {
    static Scanner scanner = new Scanner(System.in);

    public static String readCommand() {
        String command = scanner.nextLine();
        return command;
    }

    public static void printResponse(String response) {
        System.out.println("-".repeat(50));
        System.out.println(response);
        System.out.println("-".repeat(50));
    }
}