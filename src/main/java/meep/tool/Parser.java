package meep.tool;

import java.util.Arrays;
import meep.ui.Ui;

/** Routes raw user input to specific command handlers in {@link Command}. */
public class Parser {
	/**
	 * Parses and executes a command from a raw input string, and prints any
	 * non-empty response via {@link Ui#printResponse(String)}. This method also
	 * records the message.
	 *
	 * @param message
	 *            user input
	 */
	public static Command parse(String message) {
		// record message silently
		new Command.AddMessageCommand(message).execute();

		Command command = null;
		switch (message) {
			case "hello" -> command = new Command.HelloCommand();
			case "how are you?" -> command = new Command.HowAreYouCommand();
			case "list messages" -> command = new Command.ListMessagesCommand();
			case "list" -> command = new Command.ListTasksCommand();
			case "help" -> command = new Command.HelpCommand();
			default -> {
				if (message.startsWith("mark ")) {
					try {
						int taskNumber = Integer.parseInt(message.split(" ")[1]);
						command = new Command.MarkCommand(taskNumber);
					} catch (NumberFormatException e) {
						Ui.printResponse("Invalid task number.");
					}
				} else if (message.startsWith("unmark ")) {
					try {
						int taskNumber = Integer.parseInt(message.split(" ")[1]);
						command = new Command.UnmarkCommand(taskNumber);
					} catch (NumberFormatException e) {
						Ui.printResponse("Invalid task number.");
					}
				} else if (message.startsWith("delete ")) {
					try {
						int taskNumber = Integer.parseInt(message.split(" ")[1]);
						command = new Command.DeleteCommand(taskNumber);
					} catch (NumberFormatException e) {
						Ui.printResponse("Invalid task number.");
					}
				} else if (Arrays.asList("todo", "deadline", "event")
						.contains(message.split(" ", 2)[0])) {
					command = new Command.AddTaskCommand(message);
				} else if (message.startsWith("save")) {
					command = new Command.SaveCommand();
				} else if (message.startsWith("load")) {
					command = new Command.LoadCommand();
				} else if (message.startsWith("check due")) {
					command = new Command.CheckDueCommand(message);
				} else if (message.startsWith("find ")) {
					command = new Command.FindCommand(message.split(" ", 2)[1]);
				} else {
					command = new Command.UnknownCommand(message);
				}
			}
		}

		if (command != null) {
			String response = command.execute();
			if (!response.isEmpty()) {
				Ui.printResponse(response);
			}
		}
		return command;
	}

	/**
	 * Parses a command without executing or printing any response. Still records
	 * the message.
	 */
	public static Command parseQuiet(String message) {
		// record message silently
		new Command.AddMessageCommand(message).execute();
		Command command = null;
		switch (message) {
			case "hello" -> command = new Command.HelloCommand();
			case "how are you?" -> command = new Command.HowAreYouCommand();
			case "list messages" -> command = new Command.ListMessagesCommand();
			case "list" -> command = new Command.ListTasksCommand();
			case "help" -> command = new Command.HelpCommand();
			default -> {
				if (message.startsWith("mark ")) {
					int taskNumber = Integer.parseInt(message.split(" ")[1]);
					command = new Command.MarkCommand(taskNumber);
				} else if (message.startsWith("unmark ")) {
					int taskNumber = Integer.parseInt(message.split(" ")[1]);
					command = new Command.UnmarkCommand(taskNumber);
				} else if (message.startsWith("delete ")) {
					int taskNumber = Integer.parseInt(message.split(" ")[1]);
					command = new Command.DeleteCommand(taskNumber);
				} else if (Arrays.asList("todo", "deadline", "event")
						.contains(message.split(" ", 2)[0])) {
					command = new Command.AddTaskCommand(message);
				} else if (message.startsWith("save")) {
					command = new Command.SaveCommand();
				} else if (message.startsWith("load")) {
					command = new Command.LoadCommand();
				} else if (message.startsWith("check due")) {
					command = new Command.CheckDueCommand(message);
				} else if (message.startsWith("find ")) {
					command = new Command.FindCommand(message.split(" ", 2)[1]);
				} else {
					command = new Command.UnknownCommand(message);
				}
			}
		}
		return command;
	}
}
