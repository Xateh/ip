package meep.tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParserTest {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private PrintStream old;

    @BeforeEach
    void setUp() {
        old = System.out;
        System.setOut(new PrintStream(out));
        // Reset Storage path to default to avoid side effects from other tests
        Storage.setSaveFile("data/meep.txt");
    }

    @AfterEach
    void tearDown() {
        System.setOut(old);
    }

    @Test
    void routesKnownCommands() {
        Parser.parse("hello");
        Parser.parse("how are you?");
        Parser.parse("list messages");
        Parser.parse("list");
        Parser.parse("help");
        Parser.parse("find abc");
        String s = out.toString();
        assertTrue(s.contains("Hello there!"));
        assertTrue(s.contains("I'm just a program, but thanks for asking!"));
        assertTrue(s.contains("Here are all the messages I've received:"));
        assertTrue(s.contains("Here are all the tasks:"));
        assertTrue(s.contains("Here are the list of commands! [case-sensitive]"));
        assertTrue(s.contains("No tasks found matching: \"abc\""));
    }

    @Test
    void handlesTaskCommandsAndUnknowns() {
        Parser.parse("todo read");
        Parser.parse("deadline d /by 2025-08-30");
        Parser.parse("event e /from 2025-08-28 /to 2025-08-30");
        Parser.parse("mark 1");
        Parser.parse("unmark 1");
        Parser.parse("delete 1");
        Parser.parse("save");
        Parser.parse("load");
        Parser.parse("check due 2025-08-30");
        Parser.parse("unknown 123");
        String s = out.toString();
        assertTrue(s.contains("Got it. I've added this task:"));
        assertTrue(s.contains("Tasks saved successfully."));
        assertTrue(s.contains("Tasks loaded successfully."));
        assertTrue(s.contains("Checking for due tasks on "));
        assertTrue(s.contains("Unrecognised command: \"unknown\""));
        assertTrue(s.contains("unknown 123"));
    }

    @Test
    void invalidNumberForMarkUnmarkDelete() {
        out.reset();
        Parser.parse("mark abc");
        Parser.parse("unmark abc");
        Parser.parse("delete abc");
        String s = out.toString();
        // Error message is produced by Parser via Ui when NumberFormatException is
        // caught
        int occurrences = s.split("Invalid task number.", -1).length - 1;
        assertEquals(3, occurrences);
    }
}
