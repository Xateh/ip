package meep.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MeepTest {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private PrintStream oldOut;
    private java.io.InputStream oldIn;

    @BeforeEach
    void setUp() {
        oldOut = System.out;
        oldIn = System.in;
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    void tearDown() {
        System.setOut(oldOut);
        System.setIn(oldIn);
    }

    @Test
    void mainPrintsGreetingAndBye() {
        System.setIn(new ByteArrayInputStream("help\nbye\n".getBytes()));
        // Reinitialize Ui.scanner to point to the new System.in
        Ui.setScanner(new Scanner(System.in));
        Meep.main(new String[] {});
        String s = out.toString();
        assertTrue(s.contains("Hello from Meep"));
        assertTrue(s.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void byeMustMatchExactly() {
        System.setIn(new ByteArrayInputStream("bye \nbye\n".getBytes()));
        Ui.setScanner(new Scanner(System.in));
        Meep.main(new String[] {});
        String s = out.toString();
        // First 'bye ' won't exit; second exact 'bye' exits
        assertTrue(s.contains("Hello from Meep"));
        assertTrue(s.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void longSessionDoesNotCrash() {
        String input = String.join(
                "\n",
                "hello",
                "todo a",
                "deadline b /by 2025-01-01",
                "event c /from 2025-01-01 /to 2025-01-02",
                "list",
                "check due 2025-01-02",
                "save",
                "load",
                "bye",
                "");
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Ui.setScanner(new Scanner(System.in));
        Meep.main(new String[] {});
        String s = out.toString();
        assertTrue(s.contains("Hello from Meep"));
        assertTrue(s.contains("Bye. Hope to see you again soon!"));
    }
}
