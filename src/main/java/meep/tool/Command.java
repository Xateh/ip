package meep.tool;

import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Central command handler for Meep.
 *
 * <p>
 * Implements the Command pattern where each concrete operation is a nested
 * subclass overriding {@link #execute()}.
 */
public abstract class Command {
	// Shared application state for all commands
	protected static final MessageList MESSAGES = new MessageList();
	protected static final TaskList TASKS = new TaskList();

	/**
	 * Executes the command and returns the response text. Return an empty string if
	 * there is nothing to print.
	 */
	public abstract String execute();

	/** Adds a raw input message to the message list. */
	static class AddMessageCommand extends Command {
		private final String message;

		/**
		 * Creates a command that records a raw input message.
		 *
		 * @param message
		 *            the input text to record
		 */
		AddMessageCommand(String message) {
			assert message != null : "message must not be null";
			assert message != null : "message must not be null";
			this.message = message;
		}

		/** Executes and returns an empty response after recording the message. */
		@Override
		public String execute() {
			MESSAGES.addMessage(message);
			return "";
		}
	}

	/** Prints a hello response. */
	static class HelloCommand extends Command {
		/** Executes the hello command. */
		@Override
		public String execute() {
			// Keep exact phrase to satisfy tests; personality is expressed in other
			// responses
			return "Hello there!";
		}
	}

	/** Prints a canned "how are you" response. */
	static class HowAreYouCommand extends Command {
		/** Executes the canned response. */
		@Override
		public String execute() {
			// Keep original tested phrase, add personable follow-up
			return "I'm just a program, but thanks for asking! Ready when you are.";
		}
	}

	/** Lists all messages recorded. */
	static class ListMessagesCommand extends Command {
		/** Builds the list of recorded messages. */
		@Override
		public String execute() {
			StringBuilder response = new StringBuilder();
			response.append("Here are all the messages I've received:");
			MESSAGES.iterateMessages((msg, idx) -> response.append("\n " + (idx + 1) + ". " + msg));
			response.append("\nTip: type 'help' anytime to see what I can do.");
			return response.toString();
		}
	}

	/** Lists all tasks with count. */
	static class ListTasksCommand extends Command {
		/** Builds the list of tasks with count. */
		@Override
		public String execute() {
			StringBuilder response = new StringBuilder();
			response.append("Here are all the tasks:");
			TASKS.iterateTasks((task, index) -> response.append("\n " + (index + 1) + ". " + task));
			response.append("\nNow you have " + TASKS.size() + " tasks in the list.");
			response.append("\nPro tip: use 'save' to back up your changes.");
			return response.toString();
		}
	}

	/** Marks a task as done. */
	static class MarkCommand extends Command {
		private final int taskNumber; // 1-based

		/**
		 * Creates a command to mark a task as done.
		 *
		 * @param taskNumber
		 *            1-based task number to mark
		 */
		MarkCommand(int taskNumber) {
			this.taskNumber = taskNumber;
		}

		/** Marks the specified task as done if valid. */
		@Override
		public String execute() {
			StringBuilder response = new StringBuilder();
			try {
				int index = taskNumber - 1;
				TASKS.get(index).markDone();
				response.append("Task " + taskNumber + " marked as done.\n" + TASKS.get(index));
			} catch (NumberFormatException | IndexOutOfBoundsException e) {
				return ""; // maintain prior behavior: no output on invalid index
			}
			response.append("\nNice progress—keep it up!");
			return response.toString();
		}
	}

	/** Marks a task as not done. */
	static class UnmarkCommand extends Command {
		private final int taskNumber; // 1-based

		/**
		 * Creates a command to mark a task as not done.
		 *
		 * @param taskNumber
		 *            1-based task number to unmark
		 */
		UnmarkCommand(int taskNumber) {
			this.taskNumber = taskNumber;
		}

		/** Marks the specified task as not done if valid. */
		@Override
		public String execute() {
			StringBuilder response = new StringBuilder();
			try {
				int index = taskNumber - 1;
				TASKS.get(index).markNotDone();
				response.append("Task " + taskNumber + " marked as not done.\n" + TASKS.get(index));
			} catch (NumberFormatException | IndexOutOfBoundsException e) {
				return ""; // maintain prior behavior: no output on invalid index
			}
			response.append("\nNo worries—consistency beats speed.");
			return response.toString();
		}
	}

	/** Deletes the specified task. */
	static class DeleteCommand extends Command {
		private final int taskNumber; // 1-based

		/**
		 * Creates a command to delete a task.
		 *
		 * @param taskNumber
		 *            1-based task number to delete
		 */
		DeleteCommand(int taskNumber) {
			this.taskNumber = taskNumber;
		}

		/** Deletes the specified task if the index is valid. */
		@Override
		public String execute() {
			StringBuilder response = new StringBuilder();
			try {
				int index = taskNumber - 1;
				TASKS.removeTask(index);
				response.append("Task " + taskNumber + " deleted.");
			} catch (NumberFormatException | IndexOutOfBoundsException e) {
				return ""; // maintain prior behavior: no output on invalid index
			}
			response.append("\nIf that was accidental, feel free to re-add it.");
			return response.toString();
		}
	}

	/** Parses and adds a task. */
	static class AddTaskCommand extends Command {
		private final String message;

		/**
		 * Creates a command to parse and add a task from the raw command string.
		 *
		 * @param message
		 *            the full task command (e.g., "todo ...", "deadline ...", "event
		 *            ...")
		 */
		AddTaskCommand(String message) {
			assert message != null && !message.trim().isEmpty()
					: "task command must not be null or empty";
			this.message = message;
		}

		/** Parses the task and adds it, returning the outcome message. */
		@Override
		public String execute() {
			StringBuilder response = new StringBuilder();
			Pair<Task, Exception> buildPair = Task.buildTask(message);
			if (buildPair.getSecond() != null) {
				response.append(buildPair.getSecond().getMessage());
			} else {
				TASKS.addTask(buildPair.getFirst());
				response.append("Got it. I've added this task:\n" + buildPair.getFirst());
				response.append("\nNow you have " + TASKS.size() + " tasks in the list.");
			}
			response.append(
					"\nNeed due dates? Try 'check due <" + Task.getInputDtfPattern() + ">' .");
			return response.toString();
		}
	}

	/** Saves tasks to storage. */
	static class SaveCommand extends Command {
		/** Saves tasks to storage and returns a status message. */
		@Override
		public String execute() {
			StringBuilder response = new StringBuilder();
			boolean flag = Storage.saveTasks(TASKS, response);
			if (flag) {
				response.append("Tasks saved successfully.");
				response.append("\nYou're all backed up.");
			} else {
				response.append("Error saving tasks.");
				response.append("\nPlease check file permissions or disk space and try again.");
			}
			return response.toString();
		}
	}

	/** Loads tasks from storage. */
	static class LoadCommand extends Command {
		/** Loads tasks from storage and returns a status message. */
		@Override
		public String execute() {
			StringBuilder response = new StringBuilder();
			boolean flag = Storage.loadTasks(TASKS, response);
			if (flag) {
				response.append("Tasks loaded successfully.");
				response.append("\nYou're up to date.");
			} else {
				response.append("Error loading tasks.");
				response.append(
						"\nIf the file is missing or corrupted, try 'save' after adding tasks.");
			}
			return response.toString();
		}
	}

	/** Checks due tasks before a specified date. */
	static class CheckDueCommand extends Command {
		private final String message; // full command string: "check due <date>"

		/**
		 * Creates a command to check tasks due before a given date.
		 *
		 * @param message
		 *            full command string in the form "check due <date>"
		 */
		CheckDueCommand(String message) {
			assert message != null && message.startsWith("check due")
					: "expected 'check due <date>'";
			this.message = message;
		}

		/** Checks due tasks and returns a formatted report. */
		@Override
		public String execute() {
			StringBuilder response = new StringBuilder();
			String time = message.substring(9).trim();
			String processedTime = Task.printTime(time);

			if (!Task.checkTimeValid(time)) {
				response.append("Invalid date format. Please use: " + Task.getInputDtfPattern());
				return response.toString();
			}

			// Tests expect this preface line
			response.append("Checking for due tasks on ").append(processedTime).append("...");

			TASKS.iterateTasks(
					task -> {
						try {
							if (task.isDue(time)) {
								response.append("\n").append(task.toString());
							}
						} catch (DateTimeParseException e) {
							response.append("\nUnable to check due for task: " + task);
						}
					});
			// Keep additional summary header if there are due tasks; otherwise print a
			// clear none
			// message
			if (response.toString().lines().count() == 1) { // only the preface line
				response.append("\nNo tasks are due before ").append(processedTime).append(".");
			} else {
				response.append("\n");
			}

			response.append("Review these and prioritize what matters most.");
			return response.toString();
		}
	}

	/** Prints help text listing commands and usage patterns. */
	static class HelpCommand extends Command {
		/** Returns the help/usage text. */
		@Override
		public String execute() {
			StringBuilder response = new StringBuilder();
			response.append("Here are the list of commands! [case-sensitive]\n");
			response.append("\nhello:\n\tGreet the program! be polite :)");
			response.append("\nhow are you?:\n\tAsk the program how it is doing");
			response.append("\nlist messages:\n\tList all messages received");
			response.append("\nlist:\n\tList all tasks");
			response.append("\nhelp:\n\tShow this help message");
			response.append("\ntodo <todo description>: \n\tAdd a Todo Task to task list");
			response.append(
					"\n"
							+ "deadline <deadline description> /by <deadline time>: \n"
							+ "\tAdd a Deadline Task to task list (format: "
							+ Task.getInputDtfPattern()
							+ ")");
			response.append(
					"\n"
							+ "event <event description> /from <start time> /to <end time>: \n"
							+ "\tAdd an Event Task to task list (format: "
							+ Task.getInputDtfPattern()
							+ ")");
			response.append("\nmark <task number>: \n\tMark a task as done");
			response.append("\nunmark <task number>: \n\tMark a task as not done");
			response.append(
					"\n"
							+ "check due <date>: \n"
							+ "\tCheck for tasks that are due before the specified date (format: "
							+ Task.getInputDtfPattern()
							+ ")");
			response.append(
					"\n"
							+ "find <substring>: \n"
							+ "\tFind tasks whose descriptions contain the given text"
							+ " (case-sensitive)");

			return response.toString();
		}
	}

	/** Prints an unknown command message with the unrecognized keyword and echo. */
	static class UnknownCommand extends Command {
		private final String command;

		/**
		 * Creates a command that reports an unrecognised input.
		 *
		 * @param command
		 *            the full raw input
		 */
		UnknownCommand(String command) {
			assert command != null : "raw command must not be null";
			this.command = command;
		}

		/** Builds the unknown-command response, echoing the input. */
		@Override
		public String execute() {
			return "Unrecognised command: \""
					+ command.split(" ")[0]
					+ "\" Parroting...\n"
					+ command
					+ "\nTip: type 'help' to see the list of commands I understand.";
		}
	}

	/** Finds tasks containing a substring (case-sensitive). */
	static class FindCommand extends Command {
		private final String needle;

		/**
		 * Creates a command to find tasks with descriptions containing a substring.
		 *
		 * @param needle
		 *            the case-sensitive search substring
		 */
		FindCommand(String needle) {
			assert needle != null : "search needle must not be null";
			this.needle = needle;
		}

		/** Searches tasks and returns the formatted list of matches. */
		@Override
		public String execute() {
			StringBuilder response = new StringBuilder();
			List<Task> matches =
					TASKS.stream().filter(task -> task.checkDescriptionContains(needle)).toList();

			if (matches.isEmpty()) {
				response.append("No tasks found matching: \"").append(needle).append("\"");
			} else {
				response.append("Found the following tasks matching: \"")
						.append(needle)
						.append("\"");
				matches.stream()
						.forEach(
								task -> {
									response.append("\n")
											.append(matches.indexOf(task) + 1)
											.append(") ")
											.append(task);
								});
			}

			return response.toString();
		}
	}

	/** Responds with a farewell message. */
	static class ByeCommand extends Command {
		@Override
		public String execute() {
			return "Bye. Hope to see you again soon!";
		}
	}
}
