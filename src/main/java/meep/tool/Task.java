package meep.tool;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * Abstract base for tasks with Todo, Deadline and Event variants.
 *
 * <p>
 * Provides parsing helpers, serialization/deserialization, and due-date checks.
 */
public abstract class Task {
	private static String inputDtfPattern = "yyyy-MM-dd";
	private static String outputDtfPattern = "MMM dd yyyy";
	private static DateTimeFormatter inputDtf = DateTimeFormatter.ofPattern(inputDtfPattern);
	private static DateTimeFormatter outputDtf = DateTimeFormatter.ofPattern(outputDtfPattern);

	private String description;
	private boolean isDone;

	/**
	 * Serializes a task to a pipe-delimited save string. Example: |T|0|desc|,
	 * |D|1|desc|2025-01-01|, |E|0|desc|2025-01-01-2025-01-02|
	 *
	 * @param task
	 *            the task to serialize
	 * @return save string
	 */
	public static String saveString(Task task) {
		ArrayList<String> parts = new ArrayList<>();
		parts.add(
				task instanceof ToDoTask
						? "T"
						: task instanceof DeadlineTask
								? "D"
								: task instanceof EventTask ? "E" : "");
		parts.add(task.isDone() ? "1" : "0");
		parts.add(task.getDescription());
		if (task instanceof DeadlineTask) {
			parts.add(((DeadlineTask) task).getDeadline());
		} else if (task instanceof EventTask) {
			EventTask eventTask = (EventTask) task;
			parts.add(eventTask.getEventStartTime() + "-" + eventTask.getEventEndTime());
		}

		return String.format("|%s|", String.join("|", parts));
	}

	/**
	 * Deserializes a task from its pipe-delimited save string.
	 *
	 * @param saveString
	 *            the persisted representation
	 * @return reconstructed Task
	 * @throws IllegalArgumentException
	 *             if the format is invalid or type unknown
	 */
	public static Task load(String saveString) {
		String[] parts = saveString.split("\\|");
		if (parts.length < 3) {
			throw new IllegalArgumentException("Invalid task save string: " + saveString);
		} else {
			switch (parts[1]) {
				case "T" :
					return new ToDoTask(parts[3], parts[2].equals("1"));
				case "D" :
					return new DeadlineTask(parts[3], parts[4], parts[2].equals("1"));
				case "E" :
					String range = parts[4];
					if (range.length() < 21) {
						throw new ArrayIndexOutOfBoundsException("Invalid event time range");
					}
					String start = range.substring(0, 10);
					String end = range.substring(range.length() - 10);
					return new EventTask(parts[3], start, end, parts[2].equals("1"));
				default :
					throw new IllegalArgumentException("Unknown task type: " + parts[1]);
			}
		}
	}

	/**
	 * Parses a user command into a Task. Returns a Pair where either the task or
	 * the exception is non-null.
	 *
	 * @param task
	 *            raw command (e.g., "todo ...", "deadline ... /by yyyy-MM-dd",
	 *            "event ... /from ... /to ...")
	 * @return pair of (Task, Exception)
	 */
	public static Pair<Task, Exception> buildTask(String task) {
		try {
			String normalized = task.strip().replaceAll("\\s+", " ");
			if (normalized.startsWith("todo ")) {
				String desc = normalized.substring(5).trim();
				if (desc.isEmpty()) {
					throw new IllegalArgumentException("Task Description cannot be null or empty");
				}
				return new Pair<>(new ToDoTask(desc), null);
			} else if (normalized.startsWith("deadline ")) {
				return new Pair<>(new DeadlineTask(normalized.substring(9).trim()), null);
			} else if (normalized.startsWith("event ")) {
				return new Pair<>(new EventTask(normalized.substring(6).trim()), null);
			}
			return new Pair<>(null, new Exception("Specify Task Description: " + task + " <task description>"));
		} catch (Exception e) {
			return new Pair<>(null, e);
		}
	}

	protected Task(String description) {
		this(description, false);
	}

	protected Task(String description, boolean isDone) {
		assert inputDtfPattern != null && !inputDtfPattern.isEmpty() : "input pattern must be set";
		assert outputDtfPattern != null && !outputDtfPattern.isEmpty()
				: "output pattern must be set";
		if (description == null || description.trim().isEmpty()) {
			throw new IllegalArgumentException("Task Description cannot be null or empty");
		}

		this.description = description;
		this.isDone = isDone;
	}

	public String getDescription() {
		return description;
	}

	public boolean checkDescriptionContains(String substring) {
		assert substring != null : "substring must not be null";
		return description.contains(substring);
	}

	/**
	 * Returns whether the task is completed.
	 *
	 * @return true if done
	 */
	public boolean isDone() {
		return isDone;
	}

	/** Marks the task as completed. */
	public void markDone() {
		isDone = true;
	}

	/** Marks the task as not completed. */
	public void markNotDone() {
		isDone = false;
	}

	/**
	 * Validates if a date string matches the expected input format.
	 *
	 * @param time
	 *            date string
	 * @return true if parseable using the input pattern
	 */
	public static boolean checkTimeValid(String time) {
		try {
			LocalDate.parse(time, Task.inputDtf);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}

	/**
	 * Returns the expected input date format pattern.
	 *
	 * @return input pattern
	 */
	public static String getInputDtfPattern() {
		return inputDtfPattern;
	}

	/**
	 * Returns the output date format pattern used for display.
	 *
	 * @return output pattern
	 */
	public static String getOutputDtfPattern() {
		return outputDtfPattern;
	}

	/** Package-private accessor for the input formatter used by tasks. */
	static DateTimeFormatter getInputFormatter() {
		return inputDtf;
	}

	/**
	 * Determines if the task is due strictly before the given date (and not already
	 * done).
	 *
	 * @param time
	 *            date string in input format
	 * @return true if due
	 */
	public abstract boolean isDue(String time);

	/**
	 * Formats a date string for display using the output format, returning the
	 * original input if parsing fails.
	 *
	 * @param time
	 *            date string
	 * @return formatted date or original input on parse failure
	 */
	static String printTime(String time) {
		assert time != null : "time must not be null";
		try {
			LocalDate ldt = LocalDate.parse(time, inputDtf);
			return ldt.format(outputDtf);
		} catch (DateTimeParseException e) {
			return time;
		}
	}

	/**
	 * Returns a human-readable representation including completion status and
	 * description. Subclasses prepend their own type prefix (e.g., [T]/[D]/[E]).
	 */
	@Override
	public String toString() {
        String status = isDone ? "[X]" : "[ ]";
        return status + " " + description;
	}
}
