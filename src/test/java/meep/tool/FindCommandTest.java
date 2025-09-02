package meep.tool;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FindCommandTest {
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
	void findListsMatchesOrNone() {
		// prepare some tasks
		Command.addTask("todo Alpha");
		Command.addTask("deadline Beta /by 2025-12-31");
		Command.addTask("event Gamma /from 2025-12-30 /to 2026-01-02");

		out.reset();
		Command.findCommand("a"); // case-sensitive: matches Alpha and Gamma
		String s = out.toString();
		assertTrue(s.contains("Found the following tasks matching: \"a"));
		assertTrue(s.contains("Alpha"));
		assertTrue(s.contains("Gamma"));

		out.reset();
		Command.findCommand("zzz");
		String s2 = out.toString();
		assertTrue(s2.contains("No tasks found matching: \"zzz\""));
		assertFalse(s2.contains("1) "));
	}
}
