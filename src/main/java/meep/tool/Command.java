package meep.tool;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import meep.ui.Ui;

/**
 * Central command handler for Meep. Contains static methods that mutate
 * the in-memory message and task lists and print responses via {@link meep.ui.Ui}.
 */
class Command {
    private static MessageList messages = new MessageList();
    private static TaskList tasklist = new TaskList();

    /**
     * Adds a raw input message to the internal message list.
     * @param message user input
     * @return true if the operation completes
     */
    public static boolean addMessage(String message) {
        messages.addMessage(message);
        return true;
    }

    /**
     * Prints a hello response.
     * @return true always
     */
    public static boolean helloCommand() {
        Ui.printResponse("Hello there!");
        return true;
    }

    /**
     * Prints a canned "how are you" response.
     * @return true always
     */
    public static boolean howAreYouCommand() {
        Ui.printResponse("I'm just a program, but thanks for asking!");
        return true;
    }

    /**
     * Prints all stored messages in order with 1-based numbering.
     * @return true always
     */
    public static boolean listMessageCommand() {
        StringBuilder response = new StringBuilder();

        response.append("Here are all the messages I've received:");
        messages.iterateMessages((msg, idx) -> response.append("\n " + (idx + 1) + ". " + msg));

        Ui.printResponse(response.toString());
        return true;
    }

    /**
     * Prints all tasks and a summary count.
     * @return true always
     */
    public static boolean listCommand() {
        StringBuilder response = new StringBuilder();

        response.append("Here are all the tasks:");
        tasklist.iterateTasks((task, index) -> response.append("\n " + (index + 1) + ". " + task));
        response.append("\nNow you have " + tasklist.size() + " tasks in the list.");
        Ui.printResponse(response.toString());
        return true;
    }

    /**
     * Marks the given task as done.
     * @param taskNumber 1-based index of the task
     * @return true if marked; false if the index is invalid
     */
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

    /**
     * Marks the given task as not done.
     * @param taskNumber 1-based index of the task
     * @return true if unmarked; false if the index is invalid
     */
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

    /**
     * Deletes the given task.
     * @param taskNumber 1-based index of the task
     * @return true if deleted; false if the index is invalid
     */
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

    /**
     * Parses the message as a task command (todo/deadline/event) and adds it to the list.
     * @param message raw command string
     * @return true always
     */
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

    /**
     * Persists tasks to disk via {@link Storage}.
     * @return true if save succeeded, false otherwise
     */
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

    /**
     * Loads tasks from disk via {@link Storage} into the in-memory list.
     * @return true if load succeeded, false otherwise
     */
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

    /**
     * Checks and lists tasks due strictly before the given date.
     * @param message full command string starting with "check due "
     * @return true if all checks succeed; false on invalid date or parse errors
     */
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

    /**
     * Prints the help text including commands and date formats.
     */
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

    /**
     * Prints a fallback message for unrecognised commands along with the first token and echo.
     * @param command the unrecognised input
     */
    public static void unknownCommand(String command) {
        Ui.printResponse("Unrecognised command: \"" + command.split(" ")[0] + "\" Parrotting...\n" + command);
    }
}