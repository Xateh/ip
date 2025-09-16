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

	private Task(String description) {
		this(description, false);
	}

	private Task(String description, boolean isDone) {
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
		/**
		 * Creates a Todo task.
		 *
		 * @param task
		 *            description text
		 */
		ToDoTask(String task) {
			this(task, false);
		}

		/**
		 * Creates a Todo task with explicit completion state.
		 *
		 * @param task
		 *            description text
		 * @param isDone
		 *            completion flag
		 */
		ToDoTask(String task, boolean isDone) {
			super(task, isDone);
		}

		/** Returns false as Todo tasks have no due date. */
		@Override
		public boolean isDue(String time) {
			assert time != null : "time must not be null";
			return false;
		}

		/** String form prefixed with [T]. */
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

		/**
		 * Creates a Deadline task from a raw command string containing "/by".
		 *
		 * @param task
		 *            raw command string (e.g., "deadline desc /by yyyy-MM-dd")
		 */
		DeadlineTask(String task) {
			this(task.split("/", 2)[0].trim(), extractDeadline(task));
		}

		/**
		 * Creates a Deadline task with an explicit deadline.
		 *
		 * @param task
		 *            description text
		 * @param deadline
		 *            date string in input format
		 */
		DeadlineTask(String task, String deadline) {
			this(task, deadline, false);
		}

		/**
		 * Creates a Deadline task with explicit deadline and completion state.
		 *
		 * @param task
		 *            description text
		 * @param deadline
		 *            date string in input format
		 * @param isDone
		 *            completion flag
		 */
		DeadlineTask(String task, String deadline, boolean isDone) {
			super(task, isDone);
			if (deadline == null || deadline.trim().isEmpty()) {
				throw new IllegalArgumentException(
						"Deadline cannot be null or empty: Please specify deadline time with /by");
			}
			// Validate date format early and reject invalid dates
			try {
				LocalDate.parse(deadline, inputDtf);
			} catch (DateTimeParseException e) {
				throw new IllegalArgumentException("Invalid date format. Please use: " + getInputDtfPattern());
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

		/** Determines if this deadline is due before the given date. */
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

		/** String form prefixed with [D] and printed deadline. */
		@Override
		public String toString() {
			return "[D]" + super.toString() + " (by: " + printTime(getDeadline()) + ")";
		}
	}

	/** Event task spanning a start and end date. */
	private static class EventTask extends Task {
		private String eventStartTime;
		private String eventEndTime;

		/**
		 * Creates an Event task from a raw command string containing "/from" and "/to".
		 *
		 * @param task
		 *            raw command string (e.g., "event desc /from yyyy-MM-dd /to
		 *            yyyy-MM-dd")
		 */
		EventTask(String task) {
			this(
					task.split("/", 2)[0].trim(),
					EventTask.extractStartTime(task),
					EventTask.extractEndTime(task));
		}

		/**
		 * Creates an Event task with explicit start and end times.
		 *
		 * @param task
		 *            description text
		 * @param eventStartTime
		 *            start date string in input format
		 * @param eventEndTime
		 *            end date string in input format
		 */
		EventTask(String task, String eventStartTime, String eventEndTime) {
			this(task, eventStartTime, eventEndTime, false);
		}

		/**
		 * Creates an Event task with explicit times and completion state.
		 *
		 * @param task
		 *            description text
		 * @param eventStartTime
		 *            start date string in input format
		 * @param eventEndTime
		 *            end date string in input format
		 * @param isDone
		 *            completion flag
		 */
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
			// Validate dates and ensure start < end
			LocalDate start;
			LocalDate end;
			try {
				start = LocalDate.parse(eventStartTime, inputDtf);
				end = LocalDate.parse(eventEndTime, inputDtf);
			} catch (DateTimeParseException e) {
				throw new IllegalArgumentException("Invalid date format. Please use: " + getInputDtfPattern());
			}
			if (!start.isBefore(end)) {
				throw new IllegalArgumentException("Event start must be before end");
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
			int count = 0;
			String result = "";
			for (String command : task.split("/")) {
				if (command.startsWith("from")) {
					count++;
					result = command.substring(5).trim();
				}
			}
			if (count > 1) {
				throw new IllegalArgumentException("Multiple /from parameters specified");
			}
			return result;
		}

		/**
		 * Extracts the event end time from a command string.
		 *
		 * @param task
		 *            raw command
		 * @return extracted end time or empty string
		 */
		private static String extractEndTime(String task) {
			int count = 0;
			String result = "";
			for (String command : task.split("/")) {
				if (command.startsWith("to")) {
					count++;
					result = command.substring(3).trim();
				}
			}
			if (count > 1) {
				throw new IllegalArgumentException("Multiple /to parameters specified");
			}
			return result;
		}

		/**
		 * Returns the event start date string.
		 *
		 * @return start date
		 */
		public String getEventStartTime() {
			assert eventStartTime != null && !eventStartTime.isEmpty()
					: "event start time must be initialized";
			return eventStartTime;
		}

		/**
		 * Returns the event end date string.
		 *
		 * @return end date
		 */
		public String getEventEndTime() {
			assert eventEndTime != null && !eventEndTime.isEmpty()
					: "event end time must be initialized";
			return eventEndTime;
		}

		/** Determines if the event ends before the given date. */
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

		/** String form prefixed with [E] including printed start and end. */
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
