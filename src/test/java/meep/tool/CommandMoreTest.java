package meep.tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Additional coverage for various Command behaviors. */
class CommandMoreTest {
	private final ByteArrayOutputStream out = new ByteArrayOutputStream();
	private PrintStream old;

	@BeforeEach
	void setUp() {
		old = System.out;
		System.setOut(new PrintStream(out));
		Storage.setSaveFile("build/test-more-" + System.nanoTime() + ".txt");
		// Reset shared state to avoid cross-test contamination
		Command.TASKS.clearTasks();
	}

	@AfterEach
	void tearDown() {
		System.setOut(old);
	}

	@Test
	void listTasks_showsIndicesAndCount() {
		Parser.parse("todo a");
		Parser.parse("todo b");
		out.reset();
		Parser.parse("list");
		String s = out.toString();
		assertTrue(s.contains("Here are all the tasks:"));
		assertTrue(s.contains(" 1. "));
		assertTrue(s.contains(" 2. "));
		assertTrue(s.contains("Now you have 2 tasks in the list."));
	}

	@Test
	void saveAndLoad_messagesPrinted() {
		Parser.parse("todo x");
		out.reset();
		Parser.parse("save");
		String s = out.toString();
		assertTrue(s.contains("Tasks saved successfully."));

		out.reset();
		Parser.parse("load");
		String s2 = out.toString();
		assertTrue(s2.contains("Tasks loaded successfully."));
	}

	@Test
	void checkDue_invalidDateReportsFormat() {
		out.reset();
		Parser.parse("check due 2025/01/01");
		String s = out.toString();
		assertTrue(s.contains("Invalid date format. Please use:"));
		assertTrue(s.contains(Task.getInputDtfPattern()));
	}

	@Test
	void findOrdering_reflectsInsertionOrder() {
		Parser.parse("todo Alpha");
		Parser.parse("todo Beta");
		Parser.parse("todo Gamma");
		out.reset();
		Parser.parse("find a");
		String s = out.toString();
		// Alpha and Gamma appear in insertion order with 1) and 2)
		int iAlpha = s.indexOf("1) ");
		int iGamma = s.indexOf("2) ");
		assertTrue(iAlpha >= 0 && iGamma > iAlpha);
	}

	@Test
	void byeCommand_exactText() {
		out.reset();
		Parser.parse("bye");
		assertEquals(-1, out.toString().indexOf("ERROR"));
		assertTrue(out.toString().contains("Bye. Hope to see you again soon!"));
	}
}
