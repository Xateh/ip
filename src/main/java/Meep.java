import java.util.Scanner;

public class Meep {
    private static void printBorder() { System.out.println("-".repeat(50)); }

    private static void printGreeting() { printBordered("Hello from Meep!\nWhat can I do for you?"); }

    private static void printFarewell() { printBordered("Bye. Hope to see you again soon!"); }

    private static void printBordered(String message) {
        printBorder();
        System.out.println(message);
        printBorder();
    }

    public static void main(String[] args) {
        printGreeting();

        Scanner scanner = new Scanner(System.in);
        String input = "";
        input = scanner.nextLine();
        while (!input.equals("bye")) {
            printBordered(input);
            input = scanner.nextLine();
        }
        printFarewell();
        scanner.close();
    }
}
