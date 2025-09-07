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
	private boolean done;

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
					return new EventTask(
							parts[3],
							parts[4].split("-")[0],
							parts[4].split("-")[1],
							parts[2].equals("1"));
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
			return task.startsWith("todo ")
					? new Pair<>(new ToDoTask(task.substring(5).trim()), null)
					: task.startsWith("deadline ")
							? new Pair<>(new DeadlineTask(task.substring(9).trim()), null)
							: task.startsWith("event ")
									? new Pair<>(new EventTask(task.substring(6).trim()), null)
									: new Pair<>(
											null,
											new Exception(
													"Specify Task Description: "
															+ task
															+ " <task description>"));
		} catch (Exception e) {
			return new Pair<>(null, e);
		}
	}

	private Task(String description) {
		this(description, false);
	}

	private Task(String description, boolean isDone) {
	assert inputDtfPattern != null && !inputDtfPattern.isEmpty() : "input pattern must be set";
	assert outputDtfPattern != null && !outputDtfPattern.isEmpty() : "output pattern must be set";
		if (description == null || description.trim().isEmpty()) {
			throw new IllegalArgumentException("Task Description cannot be null or empty");
		}

		this.description = description;
		this.done = isDone;
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
		return done;
	}

	/** Marks the task as completed. */
	public void markDone() {
		done = true;
	}

	/** Marks the task as not completed. */
	public void markNotDone() {
		done = false;
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

	@Override
	public String toString() {
		return (isDone() ? "[X] " : "[ ] ") + getDescription();
	}

	/** Todo task with only a description. */
	private static class ToDoTask extends Task {
		ToDoTask(String task) {
			this(task, false);
		}

		ToDoTask(String task, boolean isDone) {
			super(task, isDone);
		}

		@Override
		public boolean isDue(String time) {
			assert time != null : "time must not be null";
			return false;
		}

		@Override
		public String toString() {
			return "[T]" + super.toString();
		}
	}

	/** Deadline task with a due date. */
	private static class DeadlineTask extends Task {
		private String deadline;

		/**
		 * Extracts the deadline value from a command string.
		 *
		 * @param task
		 *            raw command
		 * @return extracted deadline or empty string
		 */
		private static String extractDeadline(String task) {
			for (String command : task.split("/")) {
				if (command.startsWith("by")) {
					return command.substring(3).trim();
				}
			}
			return "";
		}

		DeadlineTask(String task) {
			this(task.split("/", 2)[0], extractDeadline(task));
		}

		DeadlineTask(String task, String deadline) {
			this(task, deadline, false);
		}

		DeadlineTask(String task, String deadline, boolean isDone) {
			super(task, isDone);
			if (deadline == null || deadline.trim().isEmpty()) {
				throw new IllegalArgumentException(
						"Deadline cannot be null or empty: Please specify deadline time with /by");
			}
			this.deadline = deadline;
		}

		/**
		 * Returns the deadline date string.
		 *
		 * @return deadline
		 */
		public String getDeadline() {
			assert deadline != null && !deadline.isEmpty() : "deadline must be initialized";
			return deadline;
		}

		@Override
		public boolean isDue(String time) {
			assert time != null : "time must not be null";
			try {
				return !isDone()
						&& LocalDate.parse(time, inputDtf)
								.isAfter(LocalDate.parse(getDeadline(), inputDtf));
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		public String toString() {
			return "[D]" + super.toString() + " (by: " + printTime(getDeadline()) + ")";
		}
	}

	/** Event task spanning a start and end date. */
	private static class EventTask extends Task {
		private String eventStartTime;
		private String eventEndTime;

		EventTask(String task) {
			this(
					task.split("/", 2)[0],
					EventTask.extractStartTime(task),
					EventTask.extractEndTime(task));
		}

		EventTask(String task, String eventStartTime, String eventEndTime) {
			this(task, eventStartTime, eventEndTime, false);
		}

		EventTask(String task, String eventStartTime, String eventEndTime, boolean isDone) {
			super(task, isDone);
			if (eventStartTime == null || eventStartTime.trim().isEmpty()) {
				throw new IllegalArgumentException(
						"Event start time cannot be null or empty: Please specify event start time"
								+ " with /from");
			}
			if (eventEndTime == null || eventEndTime.trim().isEmpty()) {
				throw new IllegalArgumentException(
						"Event end time cannot be null or empty: Please specify event end time with"
								+ " /to");
			}

			this.eventStartTime = eventStartTime;
			this.eventEndTime = eventEndTime;
		}

		/**
		 * Extracts the event start time from a command string.
		 *
		 * @param task
		 *            raw command
		 * @return extracted start time or empty string
		 */
		private static String extractStartTime(String task) {
			for (String command : task.split("/")) {
				if (command.startsWith("from")) {
					return command.substring(5).trim();
				}
			}
			return "";
		}

		/**
		 * Extracts the event end time from a command string.
		 *
		 * @param task
		 *            raw command
		 * @return extracted end time or empty string
		 */
		private static String extractEndTime(String task) {
			for (String command : task.split("/")) {
				if (command.startsWith("to")) {
					return command.substring(3).trim();
				}
			}
			return "";
		}

		/**
		 * Returns the event start date string.
		 *
		 * @return start date
		 */
		public String getEventStartTime() {
			assert eventStartTime != null && !eventStartTime.isEmpty() : "event start time must be initialized";
			return eventStartTime;
		}

		/**
		 * Returns the event end date string.
		 *
		 * @return end date
		 */
		public String getEventEndTime() {
			assert eventEndTime != null && !eventEndTime.isEmpty() : "event end time must be initialized";
			return eventEndTime;
		}

		@Override
		public boolean isDue(String time) {
			assert time != null : "time must not be null";
			try {
				return !isDone()
						&& LocalDate.parse(time, inputDtf)
								.isAfter(LocalDate.parse(getEventEndTime(), inputDtf));
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		public String toString() {
			return "[E]"
					+ super.toString()
					+ " (from: "
					+ printTime(getEventStartTime())
					+ " to: "
					+ printTime(getEventEndTime())
					+ ")";
		}
	}
}
