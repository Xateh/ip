package meep.tool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;

class Storage {
    private static String FILE_PATH = "data/meep.txt";

    public static boolean saveTasks(TaskList tasklist, StringBuilder response) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            tasklist.iterateTasks(task -> writer.println(Task.saveString(task)));
            return true;
        } catch (IOException e) {
            response.append("Error saving tasks.");
            return false;
        }
    }

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

    public static void setSaveFile(String path) {
        FILE_PATH = path;
    }
}