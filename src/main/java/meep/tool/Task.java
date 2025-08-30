package meep.tool;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public abstract class Task {
    private static String inputDtfPattern = "yyyy-MM-dd";
    private static String outputDtfPattern = "MMM dd yyyy";
    private static DateTimeFormatter inputDtf = DateTimeFormatter.ofPattern(inputDtfPattern);
    private static DateTimeFormatter outputDtf = DateTimeFormatter.ofPattern(outputDtfPattern);

    private String description;
    private boolean done;

    public static String saveString(Task task) {
        ArrayList<String> parts = new ArrayList<>();
        parts.add(
                task instanceof ToDoTask
                        ? "T"
                        : task instanceof DeadlineTask
                                ? "D"
                                : task instanceof EventTask
                                        ? "E"
                                        : "");
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

    public static Task load(String saveString) {
        String[] parts = saveString.split("\\|");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid task save string: " + saveString);
        } else {
            return switch (parts[1]) {
                case "T" -> new ToDoTask(parts[3], parts[2].equals("1"));
                case "D" -> new DeadlineTask(parts[3], parts[4], parts[2].equals("1"));
                case "E" ->
                    new EventTask(parts[3], parts[4].split("-")[0], parts[4].split("-")[1], parts[2].equals("1"));
                default -> throw new IllegalArgumentException("Unknown task type: " + parts[1]);
            };
        }
    }

    public static Pair<Task, Exception> buildTask(String task) {
        try {
            return task.startsWith("todo ")
                    ? new Pair<>(new ToDoTask(task.substring(5).trim()), null)
                    : task.startsWith("deadline ")
                            ? new Pair<>(new DeadlineTask(task.substring(9).trim()), null)
                            : task.startsWith("event ")
                                    ? new Pair<>(new EventTask(task.substring(6).trim()), null)
                                    : new Pair<>(null,
                                            new Exception("Specify Task Description: " + task + " <task description>"));
        } catch (Exception e) {
            return new Pair<>(null, e);
        }
    }

    private Task(String description) {
        this(description, false);
    }

    private Task(String description, boolean isDone) {
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
        return description.contains(substring);
    }

    public boolean isDone() {
        return done;
    }

    public void markDone() {
        done = true;
    }

    public void markNotDone() {
        done = false;
    }

    public static boolean checkTimeValid(String time) {
        try {
            LocalDate.parse(time, Task.inputDtf);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static String getInputDtfPattern() {
        return inputDtfPattern;
    }

    public static String getOutputDtfPattern() {
        return outputDtfPattern;
    }

    public abstract boolean isDue(String time);

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
        return (isDone() ? "[X] " : "[ ] ") + getDescription();
    }

    private static class ToDoTask extends Task {
        public ToDoTask(String task) {
            this(task, false);
        }

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

        private static String extractDeadline(String task) {
            for (String command : task.split("/")) {
                if (command.startsWith("by")) {
                    return command.substring(3).trim();
                }
            }
            return "";
        }

        public DeadlineTask(String task) {
            this(task.split("/", 2)[0], extractDeadline(task));
        }

        public DeadlineTask(String task, String deadline) {
            this(task, deadline, false);
        }

        public DeadlineTask(String task, String deadline, boolean isDone) {
            super(task, isDone);
            if (deadline == null || deadline.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Deadline cannot be null or empty: Please specify deadline time with /by");
            }
            this.deadline = deadline;
        }

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

        public EventTask(String task) {
            this(task.split("/", 2)[0], EventTask.extractStartTime(task), EventTask.extractEndTime(task));
        }

        public EventTask(String task, String eventStartTime, String eventEndTime) {
            this(task, eventStartTime, eventEndTime, false);

        }

        public EventTask(String task, String eventStartTime, String eventEndTime, boolean isDone) {
            super(task, isDone);
            if (eventStartTime == null || eventStartTime.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Event start time cannot be null or empty: Please specify event start time with /from");
            }
            if (eventEndTime == null || eventEndTime.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Event end time cannot be null or empty: Please specify event end time with /to");
            }

            this.eventStartTime = eventStartTime;
            this.eventEndTime = eventEndTime;
        }

        private static String extractStartTime(String task) {
            for (String command : task.split("/")) {
                if (command.startsWith("from")) {
                    return command.substring(5).trim();
                }
            }
            return "";
        }

        private static String extractEndTime(String task) {
            for (String command : task.split("/")) {
                if (command.startsWith("to")) {
                    return command.substring(3).trim();
                }
            }
            return "";
        }

        public String getEventStartTime() {
            return eventStartTime;
        }

        public String getEventEndTime() {
            return eventEndTime;
        }

        @Override
        public boolean isDue(String time) {
            try {
                return !isDone()
                        && LocalDate.parse(time, inputDtf).isAfter(LocalDate.parse(getEventEndTime(), inputDtf));
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public String toString() {
            return "[E]" + super.toString() + " (from: " + printTime(getEventStartTime()) + " to: "
                    + printTime(getEventEndTime()) + ")";
        }
    }
}
