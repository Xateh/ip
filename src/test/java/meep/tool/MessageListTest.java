package meep.tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class MessageListTest {
    @Test
    void addAndSizeAndRemoveWorks() {
        MessageList list = new MessageList();
        String s1 = list.addMessage("one");
        String s2 = list.addMessage("two");
        assertEquals(2, list.size());
        assertTrue(s1.contains("one"));
        assertTrue(s2.contains("two"));
        Message removed = list.removeMessage(0);
        assertNotNull(removed);
        assertEquals(1, list.size());
    }

    @Test
    void iterateCallbacksAreCalled() {
        MessageList list = new MessageList();
        list.addMessage("a");
        list.addMessage("b");

        AtomicInteger count = new AtomicInteger();
        StringBuilder order = new StringBuilder();
        list.iterateMessages(m -> {
            count.incrementAndGet();
            order.append(m.toString().endsWith(" a") ? "a" : "b");
        });
        assertEquals(2, count.get());
        assertEquals("ab", order.toString());

        AtomicInteger indexed = new AtomicInteger();
        list.iterateMessages((m, idx) -> {
            if (idx == 0)
                assertTrue(m.toString().endsWith(" a"));
            if (idx == 1)
                assertTrue(m.toString().endsWith(" b"));
            indexed.addAndGet(idx + 1);
        });
        assertEquals(3, indexed.get());
    }

    @Test
    void clearAndBounds() {
        MessageList list = new MessageList();
        list.addMessage("one");
        list.clearMessages();
        assertEquals(0, list.size());
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeMessage(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeMessage(-1));
    }
}
