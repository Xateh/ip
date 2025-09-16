package meep.tool;

/** Todo task with only a description. */
class ToDoTask extends Task {
    /**
     * Creates a Todo task.
     *
     * @param task description text
     */
    ToDoTask(String task) {
        this(task, false);
    }

    /**
     * Creates a Todo task with explicit completion state.
     *
     * @param task description text
     * @param isDone completion flag
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
