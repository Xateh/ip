package meep.tool;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Deadline task with a due date. */
class DeadlineTask extends Task {
	private String deadline;

	/**
	 * Extracts the deadline value from a command string.
	 *
	 * @param task
	 *            raw command
	 * @return extracted deadline or empty string
	 */
	private static String extractDeadline(String task) {
		int count = 0;
		String result = "";
		for (String command : task.split("/")) {
			if (command.startsWith("by")) {
				count++;
				result = command.substring(3).trim();
			}
		}
		if (count > 1) {
			throw new IllegalArgumentException("Multiple /by parameters specified");
		}
		return result;
	}

	/** Creates a Deadline task from a raw command string containing "/by". */
	DeadlineTask(String task) {
		this(task.split("/", 2)[0].trim(), extractDeadline(task));
	}

	/** Creates a Deadline task with an explicit deadline. */
	DeadlineTask(String task, String deadline) {
		this(task, deadline, false);
	}

	/** Creates a Deadline task with explicit deadline and completion state. */
	DeadlineTask(String task, String deadline, boolean isDone) {
		super(task, isDone);
		if (deadline == null || deadline.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"Deadline cannot be null or empty: Please specify deadline time with /by");
		}
		try {
			LocalDate.parse(deadline, Task.getInputFormatter());
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(
					"Invalid date format. Please use: " + Task.getInputDtfPattern());
		}
		this.deadline = deadline;
	}

	/** Returns the deadline date string. */
	public String getDeadline() {
		assert deadline != null && !deadline.isEmpty() : "deadline must be initialized";
		return deadline;
	}

	/** Determines if this deadline is due before the given date. */
	@Override
	public boolean isDue(String time) {
		assert time != null : "time must not be null";
		try {
			return !isDone()
					&& LocalDate.parse(time, Task.getInputFormatter())
							.isAfter(LocalDate.parse(getDeadline(), Task.getInputFormatter()));
		} catch (Exception e) {
			return false;
		}
	}

	/** String form prefixed with [D] and printed deadline. */
	@Override
	public String toString() {
		return "[D]" + super.toString() + " (by: " + Task.printTime(getDeadline()) + ")";
	}
}
