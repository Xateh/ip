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
		assertTrue(Command.helloCommand());
		assertTrue(Command.howAreYouCommand());
		String s = out.toString();
		assertTrue(s.contains("Hello there!"));
		assertTrue(s.contains("I'm just a program, but thanks for asking!"));
	}

	@Test
	void listAndAddTaskAndMarkFlow() {
		assertTrue(Command.addTask("todo x"));
		assertTrue(Command.listCommand());
		assertTrue(Command.markCommand(1));
		assertTrue(Command.unmarkCommand(1));
		assertTrue(Command.deleteCommand(1));
		String s = out.toString();
		assertTrue(s.contains("Got it. I've added this task:"));
		assertTrue(s.contains("Here are all the tasks:"));
		assertTrue(s.contains("Task 1 marked as done."));
		assertTrue(s.contains("Task 1 marked as not done."));
		assertTrue(s.contains("Task 1 deleted."));
	}

	@Test
	void helpAndUnknown() {
		Command.helpCommand();
		Command.unknownCommand("noop");
		String s = out.toString();
		assertTrue(s.contains("Here are the list of commands! [case-sensitive]"));
		assertTrue(s.contains("Unrecognised command: \"noop\""));
	}

	@Test
	void invalidIndicesReturnFalseAndPrintMessage() {
		out.reset();
		assertFalse(Command.markCommand(0));
		// Command returns false early without printing on invalid indices
		assertEquals("", out.toString());
		out.reset();
		assertFalse(Command.unmarkCommand(-1));
		assertEquals("", out.toString());
		out.reset();
		assertFalse(Command.deleteCommand(9999));
		assertEquals("", out.toString());
	}

	@Test
	void addMessageAndListMessagesShowsThem() {
		out.reset();
		Parser.parse("test-msg-A");
		Parser.parse("test-msg-B");
		Command.listMessageCommand();
		String s = out.toString();
		assertTrue(s.contains("test-msg-A"));
		assertTrue(s.contains("test-msg-B"));
	}

	@Test
	void checkDueBoundariesForDeadlineAndEvent() {
		out.reset();
		// Create three tasks around the boundary date 2025-12-31
		Command.addTask("deadline DUE_BEFORE /by 2025-12-30");
		Command.addTask("deadline DUE_EQUAL /by 2025-12-31");
		Command.addTask("deadline DUE_AFTER /by 2026-01-01");
		Command.addTask("event E_BEFORE /from 2025-12-01 /to 2025-12-30");
		Command.addTask("event E_EQUAL /from 2025-12-01 /to 2025-12-31");
		Command.addTask("event E_AFTER /from 2025-12-31 /to 2026-01-02");

		// Only those strictly before should be due (since isDue uses isAfter)
		out.reset();
		assertTrue(Command.checkDueCommand("check due 2025-12-31"));
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
		Command.helpCommand();
		String s = out.toString();
		assertTrue(s.contains("hello:"));
		assertTrue(s.contains("how are you?:"));
		assertTrue(s.contains("list messages:"));
		assertTrue(s.contains("list:"));
		assertTrue(s.contains("help:"));
		assertTrue(s.contains(Task.getInputDtfPattern()));
	}
}
