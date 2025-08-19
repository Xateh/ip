import java.util.ArrayList;
import java.util.Scanner;

public class Meep {
    private static ArrayList<String> messages = new ArrayList<>();

    private static void printBorder() { System.out.println("-".repeat(50)); }

    private static void printGreeting() { printBordered("Hello from Meep!\nWhat can I do for you?"); }

    private static void printFarewell() { printBordered("Bye. Hope to see you again soon!"); }

    private static void printBordered(String message) {
        printBorder();
        System.out.println(message);
        printBorder();
    }

    private static void processMessage(String message) {
        switch (message) {
            case "hello":
                printBordered("Hello there!");
                break;
            case "how are you?":
                printBordered("I'm just a program, but thanks for asking!");
                break;
            case "list":
                String response = "Here are all the messages I've received:";
                int num = 1;
                for (String msg : messages) {
                    response += "\n " + (num++) + ". " + msg;
                }
                printBordered(response);
                break;
            default:
                messages.add(message);
                printBordered("added: " + message);
                // printBordered("Sorry, I don't understand that.");
        }
    }

    public static void main(String[] args) {
        printGreeting();

        Scanner scanner = new Scanner(System.in);
        String message = "";
        message = scanner.nextLine();
        while (!message.equals("bye")) {
            processMessage(message);
            message = scanner.nextLine();
        }
        printFarewell();
        scanner.close();
    }
}
