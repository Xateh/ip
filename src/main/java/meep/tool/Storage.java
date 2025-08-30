package meep.tool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Persistence helper for saving and loading tasks to a simple text file.
 */
class Storage {
    private static String FILE_PATH = "data/meep.txt";

     /**
      * Saves all tasks to the configured file path.
      * Each task is written on a single line using {@link Task#saveString(Task)}.
      *
      * @param tasklist the tasks to persist
      * @param response buffer to append error messages to
      * @return true if save succeeds, false otherwise
      */
    public static boolean saveTasks(TaskList tasklist, StringBuilder response) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            tasklist.iterateTasks(task -> writer.println(Task.saveString(task)));
            return true;
        } catch (IOException e) {
            response.append("Error saving tasks.");
            return false;
        }
    }

     /**
      * Loads tasks from the configured file path into the provided task list.
      * Malformed lines are skipped and flagged via the return value.
      *
      * @param tasklist target list to populate
      * @param response buffer to append error messages to
      * @return true if all lines are loaded successfully; false if file missing or malformed entries encountered
      */
    public static boolean loadTasks(TaskList tasklist, StringBuilder response) {
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
                    tasklist.addTask(task);
                } catch (NoSuchElementException | IllegalStateException e) {
                    flag = false;
                }
            }
        } catch (IOException e) {
            response.append(FILE_PATH).append(" not found");
            flag = false;
        }
        return flag;
    }

    /**
     * Overrides the save/load file path. Intended for tests.
     *
     * @param path new file path
     */
    public static void setSaveFile(String path) {
        FILE_PATH = path;
    }
}