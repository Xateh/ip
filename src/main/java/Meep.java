import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Function;

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
        StringBuilder response = new StringBuilder();
        int num = 0;

        Function<String, StringBuilder> appendResponse = x -> response.append(x);
        Function<String, StringBuilder> appendResponseWithNewLine = x -> response.append("\n").append(x);

        switch (message) {
            case "hello":
                appendResponse.apply("Hello there!");
                break;
            case "how are you?":
                appendResponse.apply("I'm just a program, but thanks for asking!");
                break;
            case "list messages":
                appendResponse.apply("Here are all the messages I've received:");
                for (String msg : messages) {
                    appendResponse.apply("\n " + (++num) + ". " + msg);
                }
                break;
            case "list":
                appendResponse.apply("Here are all the tasks:");
                for (Task task : tasks) {
                    appendResponse.apply("\n " + (++num) + ". " + task);
                }
                appendResponse.apply("\nNow you have " + num + " tasks in the list.");
                break;
            case "help":
                appendResponseWithNewLine.apply("Here are the list of commands! [case-sensitive]\n");
                appendResponseWithNewLine.apply("hello:\n\tGreet the program! be polite :)");
                appendResponseWithNewLine.apply("how are you?:\n\tAsk the program how it is doing");
                appendResponseWithNewLine.apply("list messages:\n\tList all messages received");
                appendResponseWithNewLine.apply("list:\n\tList all tasks");
                appendResponseWithNewLine.apply("help:\n\tShow this help message");
                appendResponseWithNewLine.apply("todo <todo description>: \n\tAdd a Todo Task to task list");
                appendResponseWithNewLine.apply("deadline <deadline description> /by <deadline time>: \n\tAdd a Deadline Task to task list");
                appendResponseWithNewLine.apply("event <event description> /from <start time> /to <end time>: \n\tAdd an Event Task to task list");
                appendResponseWithNewLine.apply("mark <task number>: \n\tMark a task as done");
                appendResponseWithNewLine.apply("unmark <task number>: \n\tMark a task as not done");
                appendResponse.apply("delete <task number>: \n\tDelete a task from the list");
                break;
            default:
                if (message.startsWith("mark ")) {
                    String taskNumber = message.substring(5);
                    try {
                        int index = Integer.parseInt(taskNumber) - 1;
                        tasks.get(index).markDone();
                        appendResponse.apply("Task " + taskNumber + " marked as done.\n" + tasks.get(index));
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        appendResponse.apply("Invalid task number.");
                    }
                } else if (message.startsWith("unmark ")) {
                    String taskNumber = message.substring(7);
                    try {
                        int index = Integer.parseInt(taskNumber) - 1;
                        tasks.get(index).markNotDone();
                        appendResponse.apply("Task " + taskNumber + " marked as not done.\n" + tasks.get(index));
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        appendResponse.apply("Invalid task number.");
                    }
                } else if (message.startsWith("delete ")) {
                    String taskNumber = message.substring(7);
                    try {
                        int index = Integer.parseInt(taskNumber) - 1;
                        tasks.remove(index);
                        appendResponse.apply("Task " + taskNumber + " deleted.");
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        appendResponse.apply("Invalid task number.");
                    }
                } else if (Arrays.asList("todo", "deadline", "event").contains(message.split(" ", 2)[0])) {
                    Pair<Task, Exception> buildPair = Task.buildTask(message);
                    if (buildPair.second != null)
                        appendResponse.apply(buildPair.second.getMessage());
                    else {
                        tasks.add(buildPair.first);
                        appendResponseWithNewLine.apply("Got it. I've added this task:\n" + buildPair.first);
                        appendResponse.apply("\nNow you have " + tasks.size() + " tasks in the list.");
                    }
                } else
                    appendResponse.apply("Unrecognised command: \"" + message.split(" ")[0] + "\" Parrotting...\n" + message);
        }
        printBordered(response.toString());
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

        public static Pair<Task, Exception> buildTask(String task) {
            try {
                return task.startsWith("todo ")
                    ? new Pair<>(new ToDoTask(task.substring(5).trim()), null)
                    : task.startsWith("deadline ")
                    ? new Pair<>(new DeadlineTask(task.substring(9).trim()), null)
                    : task.startsWith("event ")
                    ? new Pair<>(new EventTask(task.substring(6).trim()), null)
                    : new Pair<>(null, new Exception("Specify Task Description: " + task + " <task description>"));
            } catch (Exception e) {
                return new Pair<>(null, e);
            }
        }

        private Task(String task) {
            if (task == null || task.trim().isEmpty()) {
                throw new IllegalArgumentException("Task Description cannot be null or empty");
            }

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

        private static class ToDoTask extends Task {
            public ToDoTask(String task) {
                super(task);
            }

            @Override
            public String toString() {
                return "[T]" + super.toString();
            }
        }

        private static class DeadlineTask extends Task {
            private String deadline;

            private static String extractDeadline(String task) {
                for (String command : task.split("/")) {
                    if (command.startsWith("by")) {
                        return command.substring(3).trim();
                    }
                }
                return "";
            }

            public DeadlineTask(String task) {
                this(task.split("/", 2)[0], extractDeadline(task));
            }

            public DeadlineTask(String task, String deadline) {
                super(task);
                if (deadline == null || deadline.trim().isEmpty()) {
                    throw new IllegalArgumentException("Deadline cannot be null or empty: Please specify deadline time with /by");
                }
                this.deadline = deadline;
            }

            public String getDeadline() {
                return deadline;
            }

            @Override
            public String toString() {
                return "[D]" + super.toString() + " (by: " + getDeadline() + ")";
            }
        }

        private static class EventTask extends Task {
            private String eventStartTime;
            private String eventEndTime;

            public EventTask(String task) {
                this(task.split("/", 2)[0], EventTask.extractStartTime(task), EventTask.extractEndTime(task));
            }

            public EventTask(String task, String eventStartTime, String eventEndTime) {
                super(task);
                if (eventStartTime == null || eventStartTime.trim().isEmpty()) {
                    throw new IllegalArgumentException("Event start time cannot be null or empty: Please specify event start time with /from");
                }
                if (eventEndTime == null || eventEndTime.trim().isEmpty()) {
                    throw new IllegalArgumentException("Event end time cannot be null or empty: Please specify event end time with /to");
                }

                this.eventStartTime = eventStartTime;
                this.eventEndTime = eventEndTime;
            }

            private static String extractStartTime(String task) {
                for (String command : task.split("/")) {
                    if (command.startsWith("from")) {
                        return command.substring(5).trim();
                    }
                }
                return "";
            }

            private static String extractEndTime(String task) {
                for (String command : task.split("/")) {
                    if (command.startsWith("to")) {
                        return command.substring(3).trim();
                    }
                }
                return "";
            }

            public String getEventStartTime() {
                return eventStartTime;
            }

            public String getEventEndTime() {
                return eventEndTime;
            }

            @Override
            public String toString() {
                return "[E]" + super.toString() + " (from: " + getEventStartTime() + " to: " + getEventEndTime() + ")";
            }
        }
    }

    private record Pair<F, S>(F first, S second) {}
}