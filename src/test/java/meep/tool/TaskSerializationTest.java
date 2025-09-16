package meep.tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Deep serialization/deserialization coverage for Task types. */
class TaskSerializationTest {
    @Test
    void roundTrip_todo_deadline_event_withDoneState() {
        Task t = Task.buildTask("todo foo").getFirst();
        t.markDone();
        String ts = Task.saveString(t);
        Task t2 = Task.load(ts);
        assertEquals(t.toString(), t2.toString());

        Task d = Task.buildTask("deadline bar /by 2025-01-02").getFirst();
        d.markDone();
        String ds = Task.saveString(d);
        Task d2 = Task.load(ds);
        assertEquals(d.toString(), d2.toString());

        Task e = Task.buildTask("event baz /from 2025-01-01 /to 2025-01-03").getFirst();
        e.markDone();
        String es = Task.saveString(e);
        Task e2 = Task.load(es);
        assertEquals(e.toString(), e2.toString());
    }

    @Test
    void invalidLoadStrings_throw() {
        assertThrows(IllegalArgumentException.class, () -> Task.load("|Z|0|what|"));
        // Missing deadline field currently triggers an index error in loader
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> Task.load("|D|1|x|"));
        assertThrows(
                ArrayIndexOutOfBoundsException.class, () -> Task.load("|E|0|x|y|")); // bad range
    }

    @Test
    void printTime_invalidFallsBack() {
        assertEquals("2025/01/01", Task.printTime("2025/01/01"));
        assertTrue(Task.printTime("2025-01-01").matches("[A-Za-z]{3} \\d{2} 2025"));
    }
}
