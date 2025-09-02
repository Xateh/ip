package meep.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UiTest {
	private final ByteArrayOutputStream out = new ByteArrayOutputStream();
	private PrintStream oldOut;

	@BeforeEach
	void setUp() {
		oldOut = System.out;
		System.setOut(new PrintStream(out));
	}

	@AfterEach
	void tearDown() {
		System.setOut(oldOut);
	}

	@Test
	void printResponseWrapsWithLines() {
		Ui.printResponse("hello");
		String s = out.toString();
		assertTrue(s.contains("hello"));
		String first = s.lines().findFirst().orElse("");
		String last = s.lines().reduce((a, b) -> b).orElse("");
		assertEquals(50, first.length());
		assertTrue(first.matches("-+"));
		assertEquals(50, last.length());
		assertTrue(last.matches("-+"));
	}

	@Test
	void printResponsePreservesMultiline() {
		out.reset();
		Ui.printResponse("line1\nline2");
		String s = out.toString();
		String[] lines = s.split("\n");
		assertEquals(4, lines.length); // top bar, line1, line2, bottom bar
		assertEquals(50, lines[0].length());
		assertEquals("line1", lines[1]);
		assertEquals("line2", lines[2]);
		assertEquals(50, lines[3].length());
	}
}
