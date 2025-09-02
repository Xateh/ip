package meep.tool;

import java.util.Arrays;
import meep.ui.Ui;

/** Routes raw user input to specific command handlers in {@link Command}. */
public class Parser {
	/**
	 * Parses and executes a command from a raw input string. This method also
	 * records the message.
	 *
	 * @param message
	 *            user input
	 */
	public static void parse(String message) {
		Command.addMessage(message);

		switch (message) {
			case "hello" :
				Command.helloCommand();
				break;
			case "how are you?" :
				Command.howAreYouCommand();
				break;
			case "list messages" :
				Command.listMessageCommand();
				break;
			case "list" :
				Command.listCommand();
				break;
			case "help" :
				Command.helpCommand();
				break;
			default :
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
				} else if (Arrays.asList("todo", "deadline", "event")
						.contains(message.split(" ", 2)[0])) {
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
