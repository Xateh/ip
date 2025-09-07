package meep.tool;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;

class DueDebugTest {
	@Test
	void dumpCheckDueOutput() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream old = System.out;
		System.setOut(new PrintStream(out));
		try {
			Parser.parse("deadline DUE_BEFORE /by 2025-12-30");
			Parser.parse("deadline DUE_EQUAL /by 2025-12-31");
			Parser.parse("deadline DUE_AFTER /by 2026-01-01");
			Parser.parse("event E_BEFORE /from 2025-12-01 /to 2025-12-30");
			Parser.parse("event E_EQUAL /from 2025-12-01 /to 2025-12-31");
			Parser.parse("event E_AFTER /from 2025-12-31 /to 2026-01-02");
			out.reset();
			Parser.parse("check due 2025-12-31");
			String s = out.toString();
			System.err.println(
					"---DEBUG CHECK DUE OUTPUT START---\n"
							+ s
							+ "\n---DEBUG CHECK DUE OUTPUT END---");
			assertTrue(s.contains("Checking for due tasks on "));
		} finally {
			System.setOut(old);
		}
	}
}
