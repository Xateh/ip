import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Function;

public class Meep {
    private static ArrayList<String> messages = new ArrayList<>();
    private static ArrayList<Task> tasks = new ArrayList<>();
    private static String saveFile = "./data/meep.txt";

    private static DateTimeFormatter inputDtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DateTimeFormatter outputDtf = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private static void printGreeting() { printBordered("Hello from Meep!\nWhat can I do for you?"); }

    private static void printFarewell() { printBordered("Bye. Hope to see you again soon!"); }

    private static void printBordered(String message) {
        Ui.printResponse("-".repeat(50));
        Ui.printResponse(message);
        Ui.printResponse("-".repeat(50));
    }

    private static void processMessage(String message) {
        messages.add(message);
        StringBuilder response = new StringBuilder();
        int num = 0;

        Function<String, StringBuilder> appendResponse = x -> response.append(x);
        Function<String, StringBuilder> appendResponseWithNewLine = x -> response.append("\n").append(x);

        switch (message) {
            case "hello" -> appendResponse.apply("Hello there!");
            case "how are you?" -> appendResponse.apply("I'm just a program, but thanks for asking!");
            case "list messages" -> {
                appendResponse.apply("Here are all the messages I've received:");
                for (String msg : messages) {
                    appendResponse.apply("\n " + (++num) + ". " + msg);
                }
            }
            case "list" -> {
                appendResponse.apply("Here are all the tasks:");
                for (Task task : tasks) {
                    appendResponse.apply("\n " + (++num) + ". " + task);
                }
                appendResponse.apply("\nNow you have " + num + " tasks in the list.");
            }
            case "help" -> {
                appendResponseWithNewLine.apply("Here are the list of commands! [case-sensitive]\n");
                appendResponseWithNewLine.apply("hello:\n\tGreet the program! be polite :)");
                appendResponseWithNewLine.apply("how are you?:\n\tAsk the program how it is doing");
                appendResponseWithNewLine.apply("list messages:\n\tList all messages received");
                appendResponseWithNewLine.apply("list:\n\tList all tasks");
                appendResponseWithNewLine.apply("help:\n\tShow this help message");
                appendResponseWithNewLine.apply("todo <todo description>: \n\tAdd a Todo Task to task list");
                appendResponseWithNewLine.apply("deadline <deadline description> /by <deadline time>: \n\tAdd a Deadline Task to task list (format: " + inputDtf + ")");
                appendResponseWithNewLine.apply("event <event description> /from <start time> /to <end time>: \n\tAdd an Event Task to task list (format: " + inputDtf + ")");
                appendResponseWithNewLine.apply("mark <task number>: \n\tMark a task as done");
                appendResponseWithNewLine.apply("unmark <task number>: \n\tMark a task as not done");
                appendResponseWithNewLine.apply("check due <date>: \n\tCheck for tasks that are due before the specified date (format: " + inputDtf + ")");
                appendResponse.apply("delete <task number>: \n\tDelete a task from the list");
            }
            default -> {
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
                } else if (message.startsWith("save")) {
                    try {
                        File file = new File(saveFile);
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                            for (Task task : tasks) {
                                writer.println(Task.saveString(task));
                            }
                        }
                        appendResponse.apply("Tasks saved to " + file.getAbsolutePath());
                    } catch (IOException e) {
                        appendResponse.apply("Error saving tasks: " + e.getMessage());
                    }
                } else if (message.startsWith("load")) {
                    try {
                        File file = new File(saveFile);
                        if (!file.exists()) {
                            appendResponse.apply("No saved tasks found.");
                        } else {
                            tasks.clear();
                            try (Scanner fileScanner = new Scanner(file)) {
                                while (fileScanner.hasNextLine()) {
                                    String line = fileScanner.nextLine();
                                    Task task = Task.load(line);
                                    tasks.add(task);
                                }
                            }
                            appendResponse.apply("Tasks loaded from " + file.getAbsolutePath());
                        }
                    } catch (IOException e) {
                        appendResponse.apply("Error loading tasks: " + e.getMessage());
                    }
                } else if (message.startsWith("check due")) {
                    String time = message.substring(9).trim();
                    String processedTime = Task.printTime(time);
                    try {
                        LocalDate.parse(time, inputDtf);
                    } catch (DateTimeParseException e) {
                        appendResponse.apply("Invalid date format. Please use yyyy-MM-dd.");
                        break;
                    }
                    appendResponse.apply("Checking for due tasks on " + processedTime + "...");

                    for (Task task : tasks) {
                        try {
                            if (task.isDue(time)) {
                                appendResponseWithNewLine.apply(task.toString());
                            }
                        } catch (DateTimeParseException e) {
                            appendResponseWithNewLine.apply("Unable to check due for task: " + task);
                        }
                    }
                } else {
                    appendResponse.apply("Unrecognised command: \"" + message.split(" ")[0] + "\" Parrotting...\n" + message);
                }
            }
        }
        printBordered(response.toString());
    }

    public static void main(String[] args) {
        printGreeting();

        String message = "";
        message = Ui.readCommand();
        while (!message.equals("bye")) {
            processMessage(message);
            message = Ui.readCommand();
        }
        printFarewell();
    }

    private abstract static class Task {
        private String task;
        private boolean done;

        public static String saveString(Task task) {
            ArrayList<String> parts = new ArrayList<>();
            parts.add(
                task instanceof ToDoTask
                            ? "T"
                            : task instanceof DeadlineTask
                            ? "D"
                            : task instanceof EventTask
                            ? "E"
                            : ""
            );
            parts.add(task.isDone() ? "1" : "0");
            parts.add(task.getTask());
            if (task instanceof DeadlineTask) {
                parts.add(((DeadlineTask) task).getDeadline());
            } else if (task instanceof EventTask) {
                EventTask eventTask = (EventTask) task;
                parts.add(eventTask.getEventStartTime() + "-" + eventTask.getEventEndTime());
            }

            return String.format("|%s|", String.join("|", parts));
        }

        private static Task load(String saveString) {
            String[] parts = saveString.split("\\|");
            if (parts.length < 3) {
                throw new IllegalArgumentException("Invalid task save string: " + saveString);
            } else {
                return switch (parts[1]) {
                    case "T" -> new ToDoTask(parts[3], parts[2].equals("1"));
                    case "D" -> new DeadlineTask(parts[3], parts[4], parts[2].equals("1"));
                    case "E" -> new EventTask(parts[3], parts[4].split("-")[0], parts[4].split("-")[1], parts[2].equals("1"));
                    default -> throw new IllegalArgumentException("Unknown task type: " + parts[1]);
                };
            }
        }

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
            this(task, false);
        }

        private Task(String task, boolean isDone) {
            if (task == null || task.trim().isEmpty()) {
                throw new IllegalArgumentException("Task Description cannot be null or empty");
            }

            this.task = task;
            this.done = isDone;
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

        public abstract boolean isDue(String time);

        private static String printTime(String time) {
            try {
                LocalDate ldt = LocalDate.parse(time, inputDtf);
                return ldt.format(outputDtf);
            } catch (DateTimeParseException e) {
                return time;
            }
        }

        @Override
        public String toString() {
            return (isDone() ? "[X] " : "[ ] ") + getTask();
        }

        private static class ToDoTask extends Task {
            public ToDoTask(String task) {
                this(task, false);
            }

            public ToDoTask(String task, boolean isDone) {
                super(task, isDone);
            }

            @Override
            public boolean isDue(String time) {
                return false;
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
                this(task, deadline, false);
            }

            public DeadlineTask(String task, String deadline, boolean isDone) {
                super(task, isDone);
                if (deadline == null || deadline.trim().isEmpty()) {
                    throw new IllegalArgumentException("Deadline cannot be null or empty: Please specify deadline time with /by");
                }
                this.deadline = deadline;
            }

            public String getDeadline() {
                return deadline;
            }
            
            @Override
            public boolean isDue(String time) {
                try {
                    return !isDone() && LocalDate.parse(time, inputDtf).isAfter(LocalDate.parse(getDeadline(), inputDtf));
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return "[D]" + super.toString() + " (by: " + printTime(getDeadline()) + ")";
            }
        }

        private static class EventTask extends Task {
            private String eventStartTime;
            private String eventEndTime;

            public EventTask(String task) {
                this(task.split("/", 2)[0], EventTask.extractStartTime(task), EventTask.extractEndTime(task));
            }

            public EventTask(String task, String eventStartTime, String eventEndTime) {
                this(task, eventStartTime, eventEndTime, false);

            }

            public EventTask(String task, String eventStartTime, String eventEndTime, boolean isDone) {
                super(task, isDone);
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
            public boolean isDue(String time) {
                try {
                    return !isDone() && LocalDate.parse(time, inputDtf).isAfter(LocalDate.parse(getEventEndTime(), inputDtf));
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public String toString() {
                return "[E]" + super.toString() + " (from: " + printTime(getEventStartTime()) + " to: " + printTime(getEventEndTime()) + ")";
            }
        }
    }

    private static class Ui {
        static Scanner scanner = new Scanner(System.in);

        public static String readCommand() {
            String command = scanner.nextLine();
            return command;
        }

        public static void printResponse(String response) {
            System.out.println(response);
        }
    }

    private record Pair<F, S>(F first, S second) {}
}

