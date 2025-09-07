package meep.tool;

import java.util.Arrays;
import meep.ui.Ui;

/**
 * Routes raw user input to specific command handlers in {@link Command}.
 *
 * <p>
 * Interactive mode prints responses and certain error messages; quiet mode only
 * builds commands without printing.
 */
public class Parser {
	/**
	 * Parses and executes a command from a raw input string and prints any
	 * non-empty response via {@link Ui#printResponse(String)}. This method also
	 * records the message.
	 *
	 * @param message
	 *            user input
	 */
	public static Command parse(String message) {
		assert message != null : "input message must not be null";
		// record message silently
		new Command.AddMessageCommand(message).execute();
		Command command = buildCommand(message, /* interactive */ true);
		if (command != null) {
			String response = command.execute();
			if (!response.isEmpty()) {
				Ui.printResponse(response);
			}
		}
		return command;
	}

	/**
	 * Parses a command without executing or printing any response; still records
	 * the message.
	 */
	public static Command parseQuiet(String message) {
		assert message != null : "input message must not be null";
		// record message silently
		new Command.AddMessageCommand(message).execute();
		return buildCommand(message, /* interactive */ false);
	}

	// Builds a Command for the given message. If interactive is true, user-facing
	// error messages are printed (e.g., invalid task number); otherwise, errors
	// are allowed to propagate for the caller to handle.
	private static Command buildCommand(String message, boolean interactive) {
		Command command = null;
		switch (message) {
			case "hello" -> command = new Command.HelloCommand();
			case "how are you?" -> command = new Command.HowAreYouCommand();
			case "list messages" -> command = new Command.ListMessagesCommand();
			case "list" -> command = new Command.ListTasksCommand();
			case "help" -> command = new Command.HelpCommand();
			case "bye" -> command = new Command.ByeCommand();
			default -> {
				if (message.startsWith("mark ")) {
					try {
						int taskNumber = Integer.parseInt(message.split(" ")[1]);
						command = new Command.MarkCommand(taskNumber);
					} catch (NumberFormatException e) {
						if (interactive) {
							Ui.printResponse("Invalid task number.");
						} else {
							throw e;
						}
					}
				} else if (message.startsWith("unmark ")) {
					try {
						int taskNumber = Integer.parseInt(message.split(" ")[1]);
						command = new Command.UnmarkCommand(taskNumber);
					} catch (NumberFormatException e) {
						if (interactive) {
							Ui.printResponse("Invalid task number.");
						} else {
							throw e;
						}
					}
				} else if (message.startsWith("delete ")) {
					try {
						int taskNumber = Integer.parseInt(message.split(" ")[1]);
						command = new Command.DeleteCommand(taskNumber);
					} catch (NumberFormatException e) {
						if (interactive) {
							Ui.printResponse("Invalid task number.");
						} else {
							throw e;
						}
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
		return command;
	}
}
