package meep.tool;

import java.util.ArrayList;

/**
 * In-memory collection of Task objects with simple iteration helpers.
 */
class TaskList {
    private final ArrayList<Task> tasks;

     /**
      * Constructs an empty task list.
      */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

     /**
      * Adds a task to the list.
      *
      * @param task the task to add
      */
    public void addTask(Task task) {
        tasks.add(task);
    }

     /**
      * Removes a task at the given zero-based index.
      *
      * @param index position in the list
      */
    public void removeTask(int index) {
        tasks.remove(index);
    }

     /**
      * Removes all tasks from the list.
      */
    public void clearTasks() {
        tasks.clear();
    }

     /**
      * Returns the task at the given zero-based index.
      *
      * @param index position in the list
      * @return the task at index
      */
    public Task get(int index) {
        return tasks.get(index);
    }

     /**
      * Returns the number of tasks in the list.
      *
      * @return task count
      */
    public int size() {
        return tasks.size();
    }

     /**
      * Iterates through tasks invoking the provided action for each task.
      *
      * @param action callback to apply to each task
      */
    public void iterateTasks(TaskAction action) {
        for (Task task : tasks) {
            action.apply(task);
        }
    }

     /**
      * Iterates through tasks invoking the provided action with the task and its index.
      *
      * @param action callback to apply to each task with index
      */
    public void iterateTasks(IndexTaskAction action) {
        for (int i = 0; i < tasks.size(); i++) {
            action.apply(tasks.get(i), i);
        }
    }

    @FunctionalInterface
    interface TaskAction {
        void apply(Task task);
    }

    @FunctionalInterface
    interface IndexTaskAction {
        void apply(Task task, int index);
    }
}
