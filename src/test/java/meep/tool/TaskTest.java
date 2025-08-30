package meep.tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TaskTest {
  @Test
  void buildTask_parsesKnownPrefixes() {
    Pair<Task, Exception> todo = Task.buildTask("todo read book");
    assertNotNull(todo.getFirst());
    assertNull(todo.getSecond());
    assertTrue(todo.getFirst().toString().startsWith("[T]"));

    Pair<Task, Exception> deadline = Task.buildTask("deadline submit /by 2025-08-30");
    assertNotNull(deadline.getFirst());
    assertNull(deadline.getSecond());
    assertTrue(deadline.getFirst().toString().startsWith("[D]"));

    Pair<Task, Exception> event = Task.buildTask("event conf /from 2025-08-28 /to 2025-08-30");
    assertNotNull(event.getFirst());
    assertNull(event.getSecond());
    assertTrue(event.getFirst().toString().startsWith("[E]"));
  }

  @Test
  void saveAndLoad_roundTrips() {
    Task d = Task.buildTask("deadline submit /by 2025-08-30").getFirst();
    String saved = Task.saveString(d);
    Task loaded = Task.load(saved);
    assertEquals(d.toString(), loaded.toString());
  }

  @Test
  void checkTimeValidAndPrintTime() {
    assertTrue(Task.checkTimeValid("2025-01-01"));
    assertFalse(Task.checkTimeValid("2025/01/01"));
    assertEquals("Jan 01 2025", Task.printTime("2025-01-01"));
  }

  @Test
  void buildTask_negativeCases() {
    Pair<Task, Exception> t1 = Task.buildTask("todo ");
    assertNull(t1.getFirst());
    assertNotNull(t1.getSecond());

    Pair<Task, Exception> d1 = Task.buildTask("deadline missing");
    assertNull(d1.getFirst());
    assertNotNull(d1.getSecond());

    Pair<Task, Exception> e1 = Task.buildTask("event onlyfrom /from 2025-01-01");
    assertNull(e1.getFirst());
    assertNotNull(e1.getSecond());

    Pair<Task, Exception> e2 = Task.buildTask("event onlyto /to 2025-01-02");
    assertNull(e2.getFirst());
    assertNotNull(e2.getSecond());
  }

  @Test
  void isDue_boundariesAndMarking() {
    Task dBefore = Task.buildTask("deadline db /by 2025-01-01").getFirst();
    Task dEqual = Task.buildTask("deadline de /by 2025-01-02").getFirst();
    Task dAfter = Task.buildTask("deadline da /by 2025-01-03").getFirst();
    assertTrue(dBefore.isDue("2025-01-02"));
    assertFalse(dEqual.isDue("2025-01-02"));
    assertFalse(dAfter.isDue("2025-01-02"));

    Task eBefore = Task.buildTask("event eb /from 2024-12-30 /to 2025-01-01").getFirst();
    Task eEqual = Task.buildTask("event ee /from 2024-12-30 /to 2025-01-02").getFirst();
    Task eAfter = Task.buildTask("event ea /from 2025-01-03 /to 2025-01-04").getFirst();
    assertTrue(eBefore.isDue("2025-01-02"));
    assertFalse(eEqual.isDue("2025-01-02"));
    assertFalse(eAfter.isDue("2025-01-02"));

    // Marking done disables due
    dBefore.markDone();
    eBefore.markDone();
    assertFalse(dBefore.isDue("2025-01-02"));
    assertFalse(eBefore.isDue("2025-01-02"));
    dBefore.markNotDone();
    assertTrue(dBefore.isDue("2025-01-02"));
  }

  @Test
  void load_invalidStringsThrow() {
    assertThrows(IllegalArgumentException.class, () -> Task.load("||"));
    assertThrows(IllegalArgumentException.class, () -> Task.load("|X|0|a|"));
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> Task.load("|E|0|a|b|"));
  }
}
