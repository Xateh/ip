package meep.tool;

import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/** Mutable collection of {@link Task} items with simple iteration helpers. */
class TaskList {
	private final ArrayList<Task> tasks;

	/** Creates an empty task list. */
	TaskList() {
		this.tasks = new ArrayList<>();
	}

	/**
	 * Adds a task to the end of the list.
	 *
	 * @param task
	 *            task to add
	 */
	public void addTask(Task task) {
	assert task != null : "task must not be null";
		tasks.add(task);
	}

	/**
	 * Removes the task at the given zero-based index.
	 *
	 * @param index
	 *            index of the task to remove
	 * @throws IndexOutOfBoundsException
	 *             if index is invalid
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
	 * @param index
	 *            index to fetch
	 * @return the task at the index
	 * @throws IndexOutOfBoundsException
	 *             if index is invalid
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
	 * @param action
	 *            callback executed for each task
	 */
	public void iterateTasks(TaskAction action) {
		tasks.forEach(action::apply);
	}

	/**
	 * Iterates all tasks with their indices, invoking the provided action in order.
	 *
	 * @param action
	 *            callback executed for each task with its index
	 */
	public void iterateTasks(IndexTaskAction action) {
		IntStream.range(0, tasks.size()).forEach(i -> action.apply(tasks.get(i), i));
	}

	/**
	 * Returns a sequential stream of tasks.
	 *
	 * @return stream over tasks
	 */
	public Stream<Task> stream() {
		return tasks.stream();
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
