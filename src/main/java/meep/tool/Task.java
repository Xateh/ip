package meep.tool;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * Base abstraction for a user task with completion status and optional time fields.
 * Provides utilities for persistence and date formatting/validation.
 */
public abstract class Task {
    private static String inputDtfPattern = "yyyy-MM-dd";
    private static String outputDtfPattern = "MMM dd yyyy";
    private static DateTimeFormatter inputDtf = DateTimeFormatter.ofPattern(inputDtfPattern);
    private static DateTimeFormatter outputDtf = DateTimeFormatter.ofPattern(outputDtfPattern);

    private String task;
    private boolean done;

    /**
     * Serialises a task into a pipe-delimited string suitable for file storage.
     *
     * @param task task to serialise
     * @return the save string representation
     */
    public static String saveString(Task task) {
        ArrayList<String> parts = new ArrayList<>();
        parts.add(
            task instanceof ToDoTask
                        ? "T"
                        : task instanceof DeadlineTask
                        ? "D"
                        : task instanceof EventTask
                        ? "E"
                        : ""
        );
        parts.add(task.isDone() ? "1" : "0");
        parts.add(task.getTask());
        if (task instanceof DeadlineTask) {
            parts.add(((DeadlineTask) task).getDeadline());
        } else if (task instanceof EventTask) {
            EventTask eventTask = (EventTask) task;
            parts.add(eventTask.getEventStartTime() + "-" + eventTask.getEventEndTime());
        }

        return String.format("|%s|", String.join("|", parts));
    }

    /**
     * Deserialises a task from a pipe-delimited save string.
     *
     * @param saveString the persisted representation
     * @return a new Task instance
     * @throws IllegalArgumentException if the string is malformed
     */
    public static Task load(String saveString) {
        String[] parts = saveString.split("\\|");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid task save string: " + saveString);
        } else {
            return switch (parts[1]) {
                case "T" -> new ToDoTask(parts[3], parts[2].equals("1"));
                case "D" -> new DeadlineTask(parts[3], parts[4], parts[2].equals("1"));
                case "E" -> new EventTask(parts[3], parts[4].split("-")[0], parts[4].split("-")[1], parts[2].equals("1"));
                default -> throw new IllegalArgumentException("Unknown task type: " + parts[1]);
            };
        }
    }

    /**
     * Builds a concrete Task from a raw user command.
     *
     * @param task raw command text
     * @return pair of (Task, Exception) where one will be non-null
     */
    public static Pair<Task, Exception> buildTask(String task) {
        try {
            return task.startsWith("todo ")
                ? new Pair<>(new ToDoTask(task.substring(5).trim()), null)
                : task.startsWith("deadline ")
                ? new Pair<>(new DeadlineTask(task.substring(9).trim()), null)
                : task.startsWith("event ")
                ? new Pair<>(new EventTask(task.substring(6).trim()), null)
                : new Pair<>(null, new Exception("Specify Task Description: " + task + " <task description>"));
        } catch (Exception e) {
            return new Pair<>(null, e);
        }
    }

    private Task(String task) {
        this(task, false);
    }

    /**
     * Constructs a Task with description and completion status.
     * Validates that the description is non-null and non-empty.
     *
     * @param task description
     * @param isDone completion status
     * @throws IllegalArgumentException if description invalid
     */
    private Task(String task, boolean isDone) {
        if (task == null || task.trim().isEmpty()) {
            throw new IllegalArgumentException("Task Description cannot be null or empty");
        }

        this.task = task;
        this.done = isDone;
    }

    /**
     * Returns the task description.
     *
     * @return description
     */
    public String getTask() {
        return task;
    }

    /**
     * Returns whether the task is marked as done.
     *
     * @return true if done
     */
    public boolean isDone() {
        return done;
    }

    /** Marks the task as done. */
    public void markDone() {
        done = true;
    }

    /** Marks the task as not done. */
    public void markNotDone() {
        done = false;
    }

    /**
     * Validates that a date string matches the expected input formatter.
     *
     * @param time date string
     * @return true if valid, false otherwise
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
     * @return formatter pattern string
     */
    public static String getInputDtfPattern() {
        return inputDtfPattern;
    }

    /**
     * Returns the human-friendly output date format pattern.
     *
     * @return formatter pattern string
     */
    public static String getOutputDtfPattern() {
        return outputDtfPattern;
    }

    public abstract boolean isDue(String time);

    /**
     * Safely pretty-prints a date string; if parsing fails, returns the original text.
     *
     * @param time input date string
     * @return pretty formatted date or original string if invalid
     */
    static String printTime(String time) {
        try {
            LocalDate ldt = LocalDate.parse(time, inputDtf);
            return ldt.format(outputDtf);
        } catch (DateTimeParseException e) {
            return time;
        }
    }

    @Override
    public String toString() {
        return (isDone() ? "[X] " : "[ ] ") + getTask();
    }

    private static class ToDoTask extends Task {
        /**
         * Constructs a todo task with the given description, not done by default.
         *
         * @param task description
         */
        public ToDoTask(String task) {
            this(task, false);
        }

        /**
         * Constructs a todo task with description and status.
         *
         * @param task description
         * @param isDone completion status
         */
        public ToDoTask(String task, boolean isDone) {
            super(task, isDone);
        }

        @Override
        public boolean isDue(String time) {
            return false;
        }

        @Override
        public String toString() {
            return "[T]" + super.toString();
        }
    }

    private static class DeadlineTask extends Task {
        private String deadline;

        /**
         * Extracts the deadline substring after "/by" from a raw command.
         * Returns an empty string if not found.
         */
        private static String extractDeadline(String task) {
            for (String command : task.split("/")) {
                if (command.startsWith("by")) {
                    return command.substring(3).trim();
                }
            }
            return "";
        }

        /**
         * Constructs a deadline task from a raw command containing "/by".
         *
         * @param task raw command
         */
        public DeadlineTask(String task) {
            this(task.split("/", 2)[0], extractDeadline(task));
        }

        /**
         * Constructs a deadline task with explicit description and deadline.
         *
         * @param task description
         * @param deadline date string matching {@link #inputDtf}
         */
        public DeadlineTask(String task, String deadline) {
            this(task, deadline, false);
        }

        /**
         * Constructs a deadline task with description, deadline and status.
         *
         * @param task description
         * @param deadline date string
         * @param isDone completion status
         */
        public DeadlineTask(String task, String deadline, boolean isDone) {
            super(task, isDone);
            if (deadline == null || deadline.trim().isEmpty()) {
                throw new IllegalArgumentException("Deadline cannot be null or empty: Please specify deadline time with /by");
            }
            this.deadline = deadline;
        }

        /** Returns the deadline string as provided by the user. */
        public String getDeadline() {
            return deadline;
        }
        
        @Override
        public boolean isDue(String time) {
            try {
                return !isDone() && LocalDate.parse(time, inputDtf).isAfter(LocalDate.parse(getDeadline(), inputDtf));
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public String toString() {
            return "[D]" + super.toString() + " (by: " + printTime(getDeadline()) + ")";
        }
    }

    private static class EventTask extends Task {
        private String eventStartTime;
        private String eventEndTime;

    /**
     * Constructs an event task from a raw command containing "/from" and "/to".
     *
     * @param task raw command
     */
        public EventTask(String task) {
            this(task.split("/", 2)[0], EventTask.extractStartTime(task), EventTask.extractEndTime(task));
        }

    /**
     * Constructs an event task with explicit description and time range.
     */
        public EventTask(String task, String eventStartTime, String eventEndTime) {
            this(task, eventStartTime, eventEndTime, false);

        }

    /**
     * Constructs an event task with description, time range and status.
     */
        public EventTask(String task, String eventStartTime, String eventEndTime, boolean isDone) {
            super(task, isDone);
            if (eventStartTime == null || eventStartTime.trim().isEmpty()) {
                throw new IllegalArgumentException("Event start time cannot be null or empty: Please specify event start time with /from");
            }
            if (eventEndTime == null || eventEndTime.trim().isEmpty()) {
                throw new IllegalArgumentException("Event end time cannot be null or empty: Please specify event end time with /to");
            }

            this.eventStartTime = eventStartTime;
            this.eventEndTime = eventEndTime;
        }

    /** Extracts the start time substring after "/from"; empty if not found. */
    private static String extractStartTime(String task) {
            for (String command : task.split("/")) {
                if (command.startsWith("from")) {
                    return command.substring(5).trim();
                }
            }
            return "";
        }

    /** Extracts the end time substring after "/to"; empty if not found. */
    private static String extractEndTime(String task) {
            for (String command : task.split("/")) {
                if (command.startsWith("to")) {
                    return command.substring(3).trim();
                }
            }
            return "";
        }

    /** Returns the event start time string. */
        public String getEventStartTime() {
            return eventStartTime;
        }

    /** Returns the event end time string. */
        public String getEventEndTime() {
            return eventEndTime;
        }

        @Override
        public boolean isDue(String time) {
            try {
                return !isDone() && LocalDate.parse(time, inputDtf).isAfter(LocalDate.parse(getEventEndTime(), inputDtf));
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public String toString() {
            return "[E]" + super.toString() + " (from: " + printTime(getEventStartTime()) + " to: " + printTime(getEventEndTime()) + ")";
        }
    }
}
