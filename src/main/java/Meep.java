import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Meep {
    public static void main(String[] args) {
        Ui.printResponse("Hello from Meep!\nWhat can I do for you?");

        String message = "";
        message = Ui.readCommand();
        while (!message.equals("bye")) {
            Parser.parse(message);
            message = Ui.readCommand();
        }
        Ui.printResponse("Bye. Hope to see you again soon!");
    }

    private abstract static class Task {
        private static String inputDtfPattern = "yyyy-MM-dd";
        private static String outputDtfPattern = "MMM dd yyyy";
        private static DateTimeFormatter inputDtf = DateTimeFormatter.ofPattern(inputDtfPattern);
        private static DateTimeFormatter outputDtf = DateTimeFormatter.ofPattern(outputDtfPattern);

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

        public static boolean checkTimeValid(String time) {
            try {
                LocalDate.parse(time, Task.inputDtf);
                return true;
            } catch (DateTimeParseException e) {
                return false;
            }
        }

        public static String getInputDtfPattern() {
            return inputDtfPattern;
        }

        public static String getOutputDtfPattern() {
            return outputDtfPattern;
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
            System.out.println("-".repeat(50));
            System.out.println(response);
            System.out.println("-".repeat(50));
        }
    }

    private static class Parser {
        private static void parse(String message) {
            Command.addMessage(message);

            switch (message) {
                case "hello" -> Command.helloCommand();
                case "how are you?" -> Command.howAreYouCommand();
                case "list messages" -> Command.listMessageCommand();
                case "list" -> Command.listCommand();
                case "help" -> Command.helpCommand();
                default -> {
                    if (message.startsWith("mark ")) {
                        try {
                            int taskNumber = Integer.parseInt(message.split(" ")[1]);
                            Command.markCommand(taskNumber);
                        } catch (NumberFormatException e) {
                            Ui.printResponse("Invalid task number.");
                        }
                    } else if (message.startsWith("unmark ")) {
                        try {
                            int taskNumber = Integer.parseInt(message.split(" ")[1]);
                            Command.unmarkCommand(taskNumber);
                        } catch (NumberFormatException e) {
                            Ui.printResponse("Invalid task number.");
                        }
                    } else if (message.startsWith("delete ")) {
                        try {
                            int taskNumber = Integer.parseInt(message.split(" ")[1]);
                            Command.deleteCommand(taskNumber);
                        } catch (NumberFormatException e) {
                            Ui.printResponse("Invalid task number.");
                        }
                    } else if (Arrays.asList("todo", "deadline", "event").contains(message.split(" ", 2)[0])) {
                        Command.addTask(message);
                    } else if (message.startsWith("save")) {
                        Command.saveCommand();
                    } else if (message.startsWith("load")) {
                        Command.loadCommand();
                    } else if (message.startsWith("check due")) {
                        Command.checkDueCommand(message);
                    } else {
                        Command.unknownCommand(message);
                    }
                }
            }
        }
    }

    private static class Command {
        private static MessageList messages = new MessageList();
        private static TaskList tasklist = new TaskList();
        private static String saveFile = "./data/meep.txt";

        public static boolean addMessage(String message) {
            messages.addMessage(message);
            return true;
        }

        public static boolean helloCommand() {
            Ui.printResponse("Hello there!");
            return true;
        }

        public static boolean howAreYouCommand() {
            Ui.printResponse("I'm just a program, but thanks for asking!");
            return true;
        }

        public static boolean listMessageCommand() {
            StringBuilder response = new StringBuilder();

            response.append("Here are all the messages I've received:");
            messages.iterateMessages((msg, idx) -> response.append("\n " + (idx + 1) + ". " + msg));

            Ui.printResponse(response.toString());
            return true;
        }

        public static boolean listCommand() {
            StringBuilder response = new StringBuilder();

            response.append("Here are all the tasks:");
            tasklist.iterateTasks((task, index) -> response.append("\n " + (index + 1) + ". " + task));
            response.append("\nNow you have " + tasklist.size() + " tasks in the list.");
            Ui.printResponse(response.toString());
            return true;
        }

        public static boolean markCommand(int taskNumber) {
            StringBuilder response = new StringBuilder();
            try {
                int index = taskNumber - 1;
                tasklist.get(index).markDone();
                response.append("Task " + taskNumber + " marked as done.\n" + tasklist.get(index));
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                response.append("Invalid task number.");
                return false;
            }
            Ui.printResponse(response.toString());
            return true;
        }

        public static boolean unmarkCommand(int taskNumber) {
            StringBuilder response = new StringBuilder();
            try {
                int index = taskNumber - 1;
                tasklist.get(index).markNotDone();
                response.append("Task " + taskNumber + " marked as not done.\n" + tasklist.get(index));
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                response.append("Invalid task number.");
                return false;
            }
            Ui.printResponse(response.toString());
            return true;
        }

        public static boolean deleteCommand(int taskNumber) {
            StringBuilder response = new StringBuilder();
            try {
                int index = taskNumber - 1;
                tasklist.removeTask(index);
                response.append("Task " + taskNumber + " deleted.");
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                response.append("Invalid task number.");
                return false;
            }
            Ui.printResponse(response.toString());
            return true;
        }

        public static boolean addTask(String message) {
            StringBuilder response = new StringBuilder();
            
            Pair<Task, Exception> buildPair = Task.buildTask(message);
            if (buildPair.second != null)
                response.append(buildPair.second.getMessage());
            else {
                tasklist.addTask(buildPair.first);
                response.append("Got it. I've added this task:\n" + buildPair.first);
                response.append("\nNow you have " + tasklist.size() + " tasks in the list.");
            }
            Ui.printResponse(response.toString());
            return true;
        }

        public static boolean saveCommand() {
            StringBuilder response = new StringBuilder();
            try {
                File file = new File(saveFile);
                if (!file.exists()) {
                    file.createNewFile();
                }
                try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                    tasklist.iterateTasks(task -> writer.println(Task.saveString(task)));
                }
                response.append("Tasks saved to " + file.getAbsolutePath());
            } catch (IOException e) {
                response.append("Error saving tasks: " + e.getMessage());
                return false;
            }
            Ui.printResponse(response.toString());
            return true;
        }

        public static boolean loadCommand() {
            StringBuilder response = new StringBuilder();
            try {
                File file = new File(saveFile);
                if (!file.exists()) {
                    response.append("No saved tasks found.");
                } else {
                    tasklist.clearTasks();
                    try (Scanner fileScanner = new Scanner(file)) {
                        while (fileScanner.hasNextLine()) {
                            String line = fileScanner.nextLine();
                            Task task = Task.load(line);
                            tasklist.addTask(task);
                        }
                    }
                    response.append("Tasks loaded from " + file.getAbsolutePath());
                }
            } catch (IOException e) {
                response.append("Error loading tasks: " + e.getMessage());
                return false;
            }
            Ui.printResponse(response.toString());
            return true;
        }

        public static boolean checkDueCommand(String message) {
            StringBuilder response = new StringBuilder();
            String time = message.substring(9).trim();
            String processedTime = Task.printTime(time);

            if (!Task.checkTimeValid(time)) {
                response.append("Invalid date format. Please use: " + Task.inputDtf);
                Ui.printResponse(response.toString());
                return false;
            }
            response.append("Checking for due tasks on " + processedTime + "...");

            ArrayList<Boolean> flags = new ArrayList<>();
            tasklist.iterateTasks(task -> {
                try {
                    if (task.isDue(time)) {
                        response.append("\n").append(task.toString());
                    }
                } catch (DateTimeParseException e) {
                    response.append("\nUnable to check due for task: " + task);
                    flags.add(false);
                }
            });

            Ui.printResponse(response.toString());
            return flags.stream().allMatch(flag -> flag);
        }

        public static void helpCommand() {
            StringBuilder response = new StringBuilder();
            response.append("Here are the list of commands! [case-sensitive]\n");
            response.append("\nhello:\n\tGreet the program! be polite :)");
            response.append("\nhow are you?:\n\tAsk the program how it is doing");
            response.append("\nlist messages:\n\tList all messages received");
            response.append("\nlist:\n\tList all tasks");
            response.append("\nhelp:\n\tShow this help message");
            response.append("\ntodo <todo description>: \n\tAdd a Todo Task to task list");
            response.append("\ndeadline <deadline description> /by <deadline time>: \n\tAdd a Deadline Task to task list (format: " + Task.getInputDtfPattern() + ")");
            response.append("\nevent <event description> /from <start time> /to <end time>: \n\tAdd an Event Task to task list (format: " + Task.getInputDtfPattern() + ")");
            response.append("\nmark <task number>: \n\tMark a task as done");
            response.append("\nunmark <task number>: \n\tMark a task as not done");
            response.append("\ncheck due <date>: \n\tCheck for tasks that are due before the specified date (format: " + Task.getInputDtfPattern() + ")");

            Ui.printResponse(response.toString());
        }

        public static void unknownCommand(String command) {
            Ui.printResponse("Unrecognised command: \"" + command.split(" ")[0] + "\" Parrotting...\n" + command);
        }
    }

    private static class TaskList {
        private final ArrayList<Task> tasks;

        public TaskList() {
            this.tasks = new ArrayList<>();
        }

        public void addTask(Task task) {
            tasks.add(task);
        }

        public void removeTask(int index) {
            tasks.remove(index);
        }

        public void clearTasks() {
            tasks.clear();
        }

        public Task get(int index) {
            return tasks.get(index);
        }

        public int size() {
            return tasks.size();
        }

        public void iterateTasks(TaskAction action) {
            for (Task task : tasks) {
                action.apply(task);
            }
        }

        public void iterateTasks(IndexTaskAction action) {
            for (int i = 0; i < tasks.size(); i++) {
                action.apply(tasks.get(i), i);
            }
        }

        @FunctionalInterface
        private interface TaskAction {
            void apply(Task task);
        }

        @FunctionalInterface
        private interface IndexTaskAction {
            void apply(Task task, int index);
        }
    }

    private static class MessageList {
        private ArrayList<Message> messages = new ArrayList<>();

        public String addMessage(String message) {
            return addMessage(new Message(message));
        }

        public String addMessage(Message message) {
            messages.add(message);
            return message.toString();
        }

        public Message removeMessage(int index) {
            return messages.remove(index);
        }

        public boolean clearMessages() {
            messages.clear();
            return true;
        }

        public int size() {
            return messages.size();
        }

        public void iterateMessages(MessageAction action) {
            for (Message message : messages) {
                action.apply(message);
            }
        }

        public void iterateMessages(IndexMessageAction action) {
            for (int i = 0; i < messages.size(); i++) {
                action.apply(messages.get(i), i);
            }
        }

        @FunctionalInterface
        private interface MessageAction {
            void apply(Message message);
        }

        @FunctionalInterface
        private interface IndexMessageAction {
            void apply(Message message, int index);
        }

        private static class Message {
            private String message;
            private LocalDateTime time;

            public Message(String message) {
                this.message = message;
                this.time = LocalDateTime.now();
            }

            @Override
            public String toString() {
                return "[" + time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] " + message;
            }
        }
    }

    private record Pair<F, S>(F first, S second) {}
}

