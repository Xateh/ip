import java.util.ArrayList;
import java.util.Scanner;

public class Meep {
    private static ArrayList<String> messages = new ArrayList<>();
    private static ArrayList<Task> tasks = new ArrayList<>();

    private static void printBorder() { System.out.println("-".repeat(50)); }

    private static void printGreeting() { printBordered("Hello from Meep!\nWhat can I do for you?"); }

    private static void printFarewell() { printBordered("Bye. Hope to see you again soon!"); }

    private static void printBordered(String message) {
        printBorder();
        System.out.println(message);
        printBorder();
    }

    private static void processMessage(String message) {
        messages.add(message);
        String response;
        int num = 1;

        switch (message) {
            case "hello":
                response = "Hello there!";
                break;
            case "how are you?":
                response = "I'm just a program, but thanks for asking!";
                break;
            case "list messages":
                response = "Here are all the messages I've received:";
                for (String msg : messages) {
                    response += "\n " + (num++) + ". " + msg;
                }
                break;
            case "list":
                response = "Here are all the tasks:";
                for (Task task : tasks) {
                    response += "\n " + (num++) + ". " + task;
                }
                break;
            default:
                if (message.startsWith("mark ")) {
                    String taskNumber = message.substring(5);
                    try {
                        int index = Integer.parseInt(taskNumber) - 1;
                        tasks.get(index).markDone();
                        response = "Task " + taskNumber + " marked as done.\n" + tasks.get(index);
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        response = "Invalid task number.";
                    }
                } else if (message.startsWith("unmark ")) {
                    String taskNumber = message.substring(7);
                    try {
                        int index = Integer.parseInt(taskNumber) - 1;
                        tasks.get(index).markNotDone();
                        response = "Task " + taskNumber + " marked as not done.\n" + tasks.get(index);
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        response = "Invalid task number.";
                    }
                } else {
                    tasks.add(new Task(message));
                    response = "added: " + message;
                    // printBordered("Sorry, I don't understand that.");
                }
        }
        printBordered(response);
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

    private static class Task {
        private String task;
        private boolean done;

        public Task(String task) {
            this.task = task;
            this.done = false;
        }

        public String getTask() {
            return task;
        }

        public boolean isDone() {
            return done;
        }

        public void markDone() {
            done = true;
        }

        public void markNotDone() {
            done = false;
        }

        @Override
        public String toString() {
            return (isDone() ? "[X] " : "[ ] ") + getTask();
        }
    }
}