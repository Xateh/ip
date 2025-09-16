package meep.tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommandTest {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private PrintStream old;

    @BeforeEach
    void setUp() {
        old = System.out;
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    void tearDown() {
        System.setOut(old);
    }

    @Test
    void helloAndHowAreYouPrint() {
        Parser.parse("hello");
        Parser.parse("how are you?");
        String s = out.toString();
        assertTrue(s.contains("Hello there!"));
        assertTrue(s.contains("I'm just a program, but thanks for asking!"));
    }

    @Test
    void listAndAddTaskAndMarkFlow() {
        Parser.parse("todo x");
        Parser.parse("list");
        Parser.parse("mark 1");
        Parser.parse("unmark 1");
        Parser.parse("delete 1");
        String s = out.toString();
        assertTrue(s.contains("Got it. I've added this task:"));
        assertTrue(s.contains("Here are all the tasks:"));
        assertTrue(s.contains("Task 1 marked as done."));
        assertTrue(s.contains("Task 1 marked as not done."));
        assertTrue(s.contains("Task 1 deleted."));
    }

    @Test
    void helpAndUnknown() {
        Parser.parse("help");
        Parser.parse("noop");
        String s = out.toString();
        assertTrue(s.contains("Here are the list of commands! [case-sensitive]"));
        assertTrue(s.contains("Unrecognised command: \"noop\""));
    }

    @Test
    void invalidIndicesReturnFalseAndPrintMessage() {
        out.reset();
        Parser.parse("mark 0");
        assertEquals("", out.toString());
        out.reset();
        Parser.parse("unmark -1");
        assertEquals("", out.toString());
        out.reset();
        Parser.parse("delete 9999");
        assertEquals("", out.toString());
    }

    @Test
    void addMessageAndListMessagesShowsThem() {
        out.reset();
        Parser.parse("test-msg-A");
        Parser.parse("test-msg-B");
        Parser.parse("list messages");
        String s = out.toString();
        assertTrue(s.contains("test-msg-A"));
        assertTrue(s.contains("test-msg-B"));
    }

    @Test
    void checkDueBoundariesForDeadlineAndEvent() {
        out.reset();
        // Create three tasks around the boundary date 2025-12-31
        Parser.parse("deadline DUE_BEFORE /by 2025-12-30");
        Parser.parse("deadline DUE_EQUAL /by 2025-12-31");
        Parser.parse("deadline DUE_AFTER /by 2026-01-01");
        Parser.parse("event E_BEFORE /from 2025-12-01 /to 2025-12-30");
        Parser.parse("event E_EQUAL /from 2025-12-01 /to 2025-12-31");
        Parser.parse("event E_AFTER /from 2025-12-31 /to 2026-01-02");

        // Only those strictly before should be due (since isDue uses isAfter)
        out.reset();
        Parser.parse("check due 2025-12-31");
        String s = out.toString();
        assertTrue(s.contains("DUE_BEFORE"));
        assertFalse(s.contains("DUE_EQUAL"));
        assertFalse(s.contains("DUE_AFTER"));
        assertTrue(s.contains("E_BEFORE"));
        assertFalse(s.contains("E_EQUAL"));
        assertFalse(s.contains("E_AFTER"));
        assertTrue(s.contains("Checking for due tasks on "));
    }

    @Test
    void helpContainsAllCommandsAndPatterns() {
        out.reset();
        Parser.parse("help");
        String s = out.toString();
        assertTrue(s.contains("hello:"));
        assertTrue(s.contains("how are you?:"));
        assertTrue(s.contains("list messages:"));
        assertTrue(s.contains("list:"));
        assertTrue(s.contains("help:"));
        assertTrue(s.contains(Task.getInputDtfPattern()));
    }
}
