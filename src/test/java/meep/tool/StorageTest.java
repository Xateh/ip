package meep.tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.junit.jupiter.api.Test;

class StorageTest {
  @Test
  void saveThenLoad_roundTrips() {
    TaskList list = new TaskList();
    list.addTask(Task.buildTask("todo a").getFirst());
    list.addTask(Task.buildTask("deadline b /by 2025-08-30").getFirst());

    String tmp = "build/test-meep-" + System.nanoTime() + ".txt";
    Storage.setSaveFile(tmp);

    StringBuilder resp = new StringBuilder();
    assertTrue(Storage.saveTasks(list, resp));
    assertEquals("", resp.toString());

    TaskList loaded = new TaskList();
    StringBuilder resp2 = new StringBuilder();
    assertTrue(Storage.loadTasks(loaded, resp2));
    assertEquals("", resp2.toString());
    assertEquals(list.size(), loaded.size());

    new File(tmp).delete();
  }

  @Test
  void loadWhenFileMissingReturnsFalse() {
    String tmp = "build/this-file-should-not-exist-" + System.nanoTime() + ".txt";
    Storage.setSaveFile(tmp);
    TaskList loaded = new TaskList();
    StringBuilder resp = new StringBuilder();
    assertFalse(new java.io.File(tmp).exists());
    assertFalse(Storage.loadTasks(loaded, resp));
    assertEquals("", resp.toString());
    assertEquals(0, loaded.size());
  }

  @Test
  void saveFailurePathReturnsFalse() {
    // Try to save to an unwritable path (a directory)
    new java.io.File("build").mkdirs();
    Storage.setSaveFile("build");
    TaskList list = new TaskList();
    list.addTask(Task.buildTask("todo a").getFirst());
    StringBuilder resp = new StringBuilder();
    assertFalse(Storage.saveTasks(list, resp));
    assertEquals("Error saving tasks.", resp.toString());
  }

  @Test
  void loadWithCorruptedLines_throwsAndKeepsPreviouslyLoaded() throws Exception {
    String tmp = "build/mixed-" + System.nanoTime() + ".txt";
    java.nio.file.Files.writeString(
        java.nio.file.Path.of(tmp),
        String.join(
            "\n",
            "|T|0|good|",
            "|X|0|bad|", // unknown type -> will throw and abort method
            "|D|0|submit|2025-12-31|"));
    Storage.setSaveFile(tmp);
    TaskList loaded = new TaskList();
    StringBuilder resp = new StringBuilder();
    assertThrows(IllegalArgumentException.class, () -> Storage.loadTasks(loaded, resp));
    // The first valid line should have been added before the exception occurred
    assertEquals(1, loaded.size());
    new java.io.File(tmp).delete();
  }
}
