package meep.tool;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Additional tests for TaskList utility methods. */
class TaskListMoreTest {
    @Test
    void containsAndStream() {
        TaskList list = new TaskList();
        Task a = Task.buildTask("todo A").getFirst();
        Task b = Task.buildTask("todo B").getFirst();
        list.addTask(a);
        assertTrue(list.contains(a));
        assertFalse(list.contains(b));

        long count = list.stream().filter(t -> t.toString().contains("A")).count();
        assertTrue(count == 1);
    }
}
