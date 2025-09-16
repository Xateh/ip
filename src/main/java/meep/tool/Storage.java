package meep.tool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Persistence layer for saving and loading {@link Task} lists from a text file.
 */
class Storage {
    private static String FILE_PATH = "data/meep.txt";

    /**
     * Saves tasks to the current file path.
     *
     * @param tasks
     *            in-memory tasks
     * @param response
     *            buffer to append error messages
     * @return true if write succeeded
     */
    public static boolean saveTasks(TaskList tasks, StringBuilder response) {
        assert tasks != null : "tasks must not be null";
        assert response != null : "response buffer must not be null";
        assert FILE_PATH != null && !FILE_PATH.isEmpty() : "FILE_PATH must be configured";
        try {
            File file = new File(FILE_PATH);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                if (!parent.mkdirs()) {
                    response.append("Error saving tasks.");
                    return false;
                }
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                tasks.iterateTasks(task -> writer.println(Task.saveString(task)));
                return true;
            }
        } catch (IOException e) {
            response.append("Error saving tasks.");
            return false;
        }
    }

    /**
     * Loads tasks from the current file path into the provided list.
     *
     * @param tasks
     *            list to populate
     * @param response
     *            buffer to append error diagnostics
     * @return true if entire load succeeded; false if file missing or read error
     */
    public static boolean loadTasks(TaskList tasks, StringBuilder response) {
        assert tasks != null : "tasks must not be null";
        assert response != null : "response buffer must not be null";
        assert FILE_PATH != null && !FILE_PATH.isEmpty() : "FILE_PATH must be configured";
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return false;
        }

        boolean flag = true;
        try (Scanner fileScanner = new Scanner(file)) {

            while (fileScanner.hasNextLine()) {
                try {
                    String line = fileScanner.nextLine();
                    Task task = Task.load(line);
                    tasks.addTask(task);
                } catch (NoSuchElementException | IllegalStateException e) {
                    flag = false;
                }
            }
        } catch (IOException e) {
            flag = false;
        }
        return flag;
    }

    public static void setSaveFile(String path) {
        assert path != null && !path.trim().isEmpty() : "save file path must not be null or empty";
        FILE_PATH = path;
    }
}
