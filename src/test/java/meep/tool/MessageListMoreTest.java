package meep.tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/** Additional tests for MessageList callbacks and bounds. */
class MessageListMoreTest {
    @Test
    void iterate_orderAndIndexing() {
        MessageList list = new MessageList();
        list.addMessage("first");
        list.addMessage("second");

        StringBuilder sb = new StringBuilder();
        list.iterateMessages(m -> sb.append(m.toString().endsWith(" first") ? "1" : "2"));
        assertEquals("12", sb.toString());

        AtomicInteger idxSum = new AtomicInteger();
        list.iterateMessages((m, idx) -> idxSum.addAndGet(idx + 1));
        assertEquals(3, idxSum.get());
    }

    @Test
    void boundsOnRemove() {
        MessageList list = new MessageList();
        list.addMessage("only");
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeMessage(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeMessage(-1));
        assertTrue(list.size() > 0);
    }
}
