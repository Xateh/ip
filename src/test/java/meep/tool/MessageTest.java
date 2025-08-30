package meep.tool;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MessageTest {
    @Test
    void toStringContainsMessage() {
        Message m = new Message("hello");
        assertTrue(m.toString().endsWith(" hello"));
        assertTrue(m.toString().startsWith("["));
    }

    @Test
    void toStringTimestampFormatIsCorrect() {
        Message m = new Message("x");
        String s = m.toString();
        // [YYYY-MM-DD HH:MM:SS] x
        String regex = "\\[\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\] x";
        assertTrue(s.matches(regex));
    }
}
