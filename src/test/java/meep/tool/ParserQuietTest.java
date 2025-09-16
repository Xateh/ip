package meep.tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Additional coverage for Parser.parseQuiet and whitespace normalization. */
class ParserQuietTest {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private PrintStream old;

    @BeforeEach
    void setUp() {
        old = System.out;
        System.setOut(new PrintStream(out));
        Storage.setSaveFile("data/meep.txt");
    }

    @AfterEach
    void tearDown() {
        System.setOut(old);
    }

    @Test
    void parseQuiet_buildsCommandWithoutPrinting() {
        out.reset();
        Command c = Parser.parseQuiet("hello");
        // Should not print anything in quiet mode
        assertEquals("", out.toString());
        assertNotNull(c);
        assertTrue(c instanceof Command.HelloCommand);
    }

    @Test
    void normalization_trimsAndSquashesSpaces() {
        out.reset();
        Command c = Parser.parseQuiet("   find    abc   ");
        assertEquals("", out.toString());
        assertNotNull(c);
        // Should be routed as FindCommand with needle "abc"
        String s = c.execute();
        assertTrue(s.contains("No tasks found matching: \"abc\""));
    }

    @Test
    void unknownCommand_isBuiltWithoutPrinting() {
        out.reset();
        Command c = Parser.parseQuiet("doesnotexist 123");
        assertEquals("", out.toString());
        assertNotNull(c);
        String s = c.execute();
        assertTrue(s.contains("Unrecognised command: \"doesnotexist\""));
    }
}
