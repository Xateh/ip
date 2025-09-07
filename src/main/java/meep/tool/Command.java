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
	protected static final MessageList messages = new MessageList();
	protected static final TaskList tasklist = new TaskList();

	/**
	 * Executes the command and returns the response text. Return an empty string if
	 * there is nothing to print.
	 */
	public abstract String execute();

	/** Adds a raw input message to the message list. */
	static class AddMessageCommand extends Command {
		private final String message;

		AddMessageCommand(String message) {
			assert message != null : "message must not be null";
			this.message = message;
		}

		@Override
		public String execute() {
			messages.addMessage(message);
			return "";
		}
	}

	/** Prints a hello response. */
	static class HelloCommand extends Command {
		@Override
		public String execute() {
			return "Hello there!";
		}
	}

	/** Prints a canned "how are you" response. */
	static class HowAreYouCommand extends Command {
		@Override
		public String execute() {
			return "I'm just a program, but thanks for asking!";
		}
	}

	/** Lists all messages recorded. */
	static class ListMessagesCommand extends Command {
		@Override
		public String execute() {
			StringBuilder response = new StringBuilder();
			response.append("Here are all the messages I've received:");
			messages.iterateMessages((msg, idx) -> response.append("\n " + (idx + 1) + ". " + msg));
			return response.toString();
		}
	}

	/** Lists all tasks with count. */
	static class ListTasksCommand extends Command {
		@Override
		public String execute() {
			StringBuilder response = new StringBuilder();
			response.append("Here are all the tasks:");
			tasklist.iterateTasks(
					(task, index) -> response.append("\n " + (index + 1) + ". " + task));
			response.append("\nNow you have " + tasklist.size() + " tasks in the list.");
			return response.toString();
		}
	}

	/** Marks a task as done. */
	static class MarkCommand extends Command {
		private final int taskNumber; // 1-based

		MarkCommand(int taskNumber) {
			this.taskNumber = taskNumber;
		}

		@Override
		public String execute() {
			StringBuilder response = new StringBuilder();
			try {
				int index = taskNumber - 1;
				tasklist.get(index).markDone();
				response.append("Task " + taskNumber + " marked as done.\n" + tasklist.get(index));
			} catch (NumberFormatException | IndexOutOfBoundsException e) {
				return ""; // maintain prior behavior: no output on invalid index
			}
			return response.toString();
		}
	}

	/** Marks a task as not done. */
	static class UnmarkCommand extends Command {
		private final int taskNumber; // 1-based

		UnmarkCommand(int taskNumber) {
			this.taskNumber = taskNumber;
		}

		@Override
		public String execute() {
			StringBuilder response = new StringBuilder();
			try {
				int index = taskNumber - 1;
				tasklist.get(index).markNotDone();
				response.append(
						"Task " + taskNumber + " marked as not done.\n" + tasklist.get(index));
			} catch (NumberFormatException | IndexOutOfBoundsException e) {
				return ""; // maintain prior behavior: no output on invalid index
			}
			return response.toString();
		}
	}

	/** Deletes the specified task. */
	static class DeleteCommand extends Command {
		private final int taskNumber; // 1-based

		DeleteCommand(int taskNumber) {
			this.taskNumber = taskNumber;
		}

		@Override
		public String execute() {
			StringBuilder response = new StringBuilder();
			try {
				int index = taskNumber - 1;
				tasklist.removeTask(index);
				response.append("Task " + taskNumber + " deleted.");
			} catch (NumberFormatException | IndexOutOfBoundsException e) {
				return ""; // maintain prior behavior: no output on invalid index
			}
			return response.toString();
		}
	}

	/** Parses and adds a task. */
	static class AddTaskCommand extends Command {
		private final String message;

		AddTaskCommand(String message) {
			assert message != null && !message.trim().isEmpty()
					: "task command must not be null or empty";
			this.message = message;
		}

		@Override
		public String execute() {
			StringBuilder response = new StringBuilder();
			Pair<Task, Exception> buildPair = Task.buildTask(message);
			if (buildPair.getSecond() != null) {
				response.append(buildPair.getSecond().getMessage());
			} else {
				tasklist.addTask(buildPair.getFirst());
				response.append("Got it. I've added this task:\n" + buildPair.getFirst());
				response.append("\nNow you have " + tasklist.size() + " tasks in the list.");
			}
			return response.toString();
		}
	}

	/** Saves tasks to storage. */
	static class SaveCommand extends Command {
		@Override
		public String execute() {
			StringBuilder response = new StringBuilder();
			boolean flag = Storage.saveTasks(tasklist, response);
			if (flag) {
				response.append("Tasks saved successfully.");
			} else {
				response.append("Error saving tasks.");
			}
			return response.toString();
		}
	}

	/** Loads tasks from storage. */
	static class LoadCommand extends Command {
		@Override
		public String execute() {
			StringBuilder response = new StringBuilder();
			boolean flag = Storage.loadTasks(tasklist, response);
			if (flag) {
				response.append("Tasks loaded successfully.");
			} else {
				response.append("Error loading tasks.");
			}
			return response.toString();
		}
	}

	/** Checks due tasks before a specified date. */
	static class CheckDueCommand extends Command {
		private final String message; // full command string: "check due <date>"

		CheckDueCommand(String message) {
			assert message != null && message.startsWith("check due")
					: "expected 'check due <date>'";
			this.message = message;
		}

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

			tasklist.iterateTasks(
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

			return response.toString();
		}
	}

	/** Prints help text listing commands and usage patterns. */
	static class HelpCommand extends Command {
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

		UnknownCommand(String command) {
			this.command = command;
		}

		@Override
		public String execute() {
			return "Unrecognised command: \""
					+ command.split(" ")[0]
					+ "\" Parroting...\n"
					+ command;
		}
	}

	/** Finds tasks containing a substring (case-sensitive). */
	static class FindCommand extends Command {
		private final String needle;

		FindCommand(String needle) {
			this.needle = needle;
		}

		@Override
		public String execute() {
			StringBuilder response = new StringBuilder();
			List<Task> matches =
					tasklist.stream()
							.filter(task -> task.checkDescriptionContains(needle))
							.toList();

			if (matches.isEmpty()) {
				response.append("No tasks found matching: \"").append(needle).append("\"");
			} else {
				response.append("Found the following tasks matching: \"")
						.append(needle)
						.append("\"");
				for (int i = 0; i < matches.size(); i++) {
					response.append("\n").append(i + 1).append(") ").append(matches.get(i));
				}
			}

			return response.toString();
		}
	}
}
