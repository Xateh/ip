package meep.tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Additional edge-case coverage for Storage. */
class StorageEdgeTest {
    private Path tempDir;

    @AfterEach
    void cleanup() throws Exception {
        // Best-effort cleanup when using temp directories
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted(java.util.Comparator.reverseOrder())
                    .forEach(p -> p.toFile().delete());
        }
    }

    @Test
    void createsMissingDirectoriesOnSave() throws Exception {
        tempDir = Files.createTempDirectory("meep-storage");
        Path nested = tempDir.resolve("a/b/c/meep.txt");
        Storage.setSaveFile(nested.toString());

        TaskList list = new TaskList();
        list.addTask(Task.buildTask("todo hello").getFirst());
        StringBuilder resp = new StringBuilder();
        assertTrue(Storage.saveTasks(list, resp));
        assertEquals("", resp.toString());
        assertTrue(Files.exists(nested));
    }

    @Test
    void saveToUnwritablePath_appendsGenericError() {
        // Point to a directory (common unwritable target for a file write)
        new File("build").mkdirs();
        Storage.setSaveFile("build");

        TaskList list = new TaskList();
        list.addTask(Task.buildTask("todo a").getFirst());
        StringBuilder resp = new StringBuilder();
        assertFalse(Storage.saveTasks(list, resp));
        assertEquals("Error saving tasks.", resp.toString());
    }
}
