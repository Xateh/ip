package meep.tool;

import java.util.Arrays;

import meep.ui.Ui;

public class Parser {
    public static void parse(String message) {
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
                } else if (message.startsWith("find ")) {
                    Command.findCommand(message.split(" ", 2)[1]);
                } else {
                    Command.unknownCommand(message);
                }
            }
        }
    }
}
