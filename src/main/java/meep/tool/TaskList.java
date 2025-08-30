package meep.tool;

import java.util.ArrayList;

/** Mutable collection of {@link Task} items with simple iteration helpers. */
class TaskList {
  private final ArrayList<Task> tasks;

  /** Creates an empty task list. */
  public TaskList() {
    this.tasks = new ArrayList<>();
  }

  /**
   * Adds a task to the end of the list.
   *
   * @param task task to add
   */
  public void addTask(Task task) {
    tasks.add(task);
  }

  /**
   * Removes the task at the given zero-based index.
   *
   * @param index index of the task to remove
   * @throws IndexOutOfBoundsException if index is invalid
   */
  public void removeTask(int index) {
    tasks.remove(index);
  }

  /** Removes all tasks from the list. */
  public void clearTasks() {
    tasks.clear();
  }

  /**
   * Returns the task at the given zero-based index.
   *
   * @param index index to fetch
   * @return the task at the index
   * @throws IndexOutOfBoundsException if index is invalid
   */
  public Task get(int index) {
    return tasks.get(index);
  }

  /**
   * Returns the number of tasks currently stored.
   *
   * @return size of the list
   */
  public int size() {
    return tasks.size();
  }

  /**
   * Iterates all tasks, invoking the provided action in order.
   *
   * @param action callback executed for each task
   */
  public void iterateTasks(TaskAction action) {
    for (Task task : tasks) {
      action.apply(task);
    }
  }

  /**
   * Iterates all tasks with their indices, invoking the provided action in order.
   *
   * @param action callback executed for each task with its index
   */
  public void iterateTasks(IndexTaskAction action) {
    for (int i = 0; i < tasks.size(); i++) {
      action.apply(tasks.get(i), i);
    }
  }

  @FunctionalInterface
  /** Functional callback for iterating tasks without indices. */
  interface TaskAction {
    void apply(Task task);
  }

  @FunctionalInterface
  /** Functional callback for iterating tasks with indices. */
  interface IndexTaskAction {
    void apply(Task task, int index);
  }
}
