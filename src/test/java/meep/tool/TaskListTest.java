package meep.tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void addGetRemoveSizeIterate() {
        TaskList list = new TaskList();
        Task t1 = Task.buildTask("todo t1").getFirst();
        Task t2 = Task.buildTask("todo t2").getFirst();
        list.addTask(t1);
        list.addTask(t2);
        assertEquals(2, list.size());
        assertEquals(t1.toString(), list.get(0).toString());
        list.removeTask(0);
        assertEquals(1, list.size());

        AtomicInteger count = new AtomicInteger();
        StringBuilder order = new StringBuilder();
        list.iterateTasks(
                task -> {
                    count.incrementAndGet();
                    order.append(task.toString().contains("t2") ? "2" : "?");
                });
        assertEquals(1, count.get());
        assertEquals("2", order.toString());

        AtomicInteger sumIdx = new AtomicInteger();
        list.iterateTasks(
                (task, idx) -> {
                    if (idx == 0) {
                        assertTrue(task.toString().contains("t2"));
                    }
                    sumIdx.addAndGet(idx + 1);
                });
        assertEquals(1, sumIdx.get());
    }

    @Test
    void errorAndClearBehaviors() {
        TaskList list = new TaskList();
        list.addTask(Task.buildTask("todo t1").getFirst());
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(99));
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeTask(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeTask(99));
        list.clearTasks();
        assertEquals(0, list.size());
        // iterators no-op
        list.iterateTasks(t -> fail("should not be called on empty"));
        list.iterateTasks((t, i) -> fail("should not be called on empty"));
    }
}
