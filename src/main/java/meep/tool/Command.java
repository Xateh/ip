package meep.tool;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import meep.ui.Ui;

class Command {
    private static MessageList messages = new MessageList();
    private static TaskList tasklist = new TaskList();

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
        if (buildPair.getSecond() != null)
            response.append(buildPair.getSecond().getMessage());
        else {
            tasklist.addTask(buildPair.getFirst());
            response.append("Got it. I've added this task:\n" + buildPair.getFirst());
            response.append("\nNow you have " + tasklist.size() + " tasks in the list.");
        }
        Ui.printResponse(response.toString());
        return true;
    }

    public static boolean saveCommand() {
        StringBuilder response = new StringBuilder();
        boolean flag = Storage.saveTasks(tasklist, response);
        if (flag) {
            response.append("Tasks saved successfully.");
        } else {
            response.append("Error saving tasks.");
        }
        Ui.printResponse(response.toString());
        return flag;
    }

    public static boolean loadCommand() {
        StringBuilder response = new StringBuilder();
        boolean flag = Storage.loadTasks(tasklist, response);
        if (flag) {
            response.append("Tasks loaded successfully.");
        } else {
            response.append("Error loading tasks.");
        }
        Ui.printResponse(response.toString());
        return flag;
    }

    public static boolean checkDueCommand(String message) {
        StringBuilder response = new StringBuilder();
        String time = message.substring(9).trim();
        String processedTime = Task.printTime(time);

        if (!Task.checkTimeValid(time)) {
            response.append("Invalid date format. Please use: " + Task.getInputDtfPattern());
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