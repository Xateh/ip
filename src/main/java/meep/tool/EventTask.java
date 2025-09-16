package meep.tool;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Event task spanning a start and end date. */
class EventTask extends Task {
    private String eventStartTime;
    private String eventEndTime;

    /**
     * Creates an Event task from a raw command string containing "/from" and "/to".
     */
    EventTask(String task) {
        this(task.split("/", 2)[0].trim(), extractStartTime(task), extractEndTime(task));
    }

    /** Creates an Event task with explicit start and end times. */
    EventTask(String task, String eventStartTime, String eventEndTime) {
        this(task, eventStartTime, eventEndTime, false);
    }

    /** Creates an Event task with explicit times and completion state. */
    EventTask(String task, String eventStartTime, String eventEndTime, boolean isDone) {
        super(task, isDone);
        if (eventStartTime == null || eventStartTime.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Event start time cannot be null or empty: Please specify event start time with"
                            + " /from");
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
            start = LocalDate.parse(eventStartTime, Task.getInputFormatter());
            end = LocalDate.parse(eventEndTime, Task.getInputFormatter());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid date format. Please use: " + Task.getInputDtfPattern());
        }
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Event start must be before end");
        }
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
    }

    /** Extracts the event start time from a command string. */
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

    /** Extracts the event end time from a command string. */
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

    /** Returns the event start date string. */
    public String getEventStartTime() {
        assert eventStartTime != null && !eventStartTime.isEmpty()
                : "event start time must be initialized";
        return eventStartTime;
    }

    /** Returns the event end date string. */
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
                    && LocalDate.parse(time, Task.getInputFormatter())
                            .isAfter(LocalDate.parse(getEventEndTime(), Task.getInputFormatter()));
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
                + Task.printTime(getEventStartTime())
                + " to: "
                + Task.printTime(getEventEndTime())
                + ")";
    }
}
