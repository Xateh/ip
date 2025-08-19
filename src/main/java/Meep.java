public class Meep {
    private static void printBorder() {
        System.out.println("-".repeat(50));
    }

    private static void printGreeting() {
        System.out.println("Hello from Meep!");
        System.out.println("What can I do for you?");
    }

    private static void printFarewell() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    public static void main(String[] args) {
        printBorder();
        printGreeting();

        printBorder();
        printFarewell();
    }
}
