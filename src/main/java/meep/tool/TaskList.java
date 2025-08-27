package meep.tool;

import java.util.ArrayList;

class TaskList {
    private final ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(int index) {
        tasks.remove(index);
    }

    public void clearTasks() {
        tasks.clear();
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    public void iterateTasks(TaskAction action) {
        for (Task task : tasks) {
            action.apply(task);
        }
    }

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
