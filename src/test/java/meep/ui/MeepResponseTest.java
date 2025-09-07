package meep.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import meep.tool.Pair;
import org.junit.jupiter.api.Test;

class MeepResponseTest {
	@Test
	void getResponseReturnsTextAndType() {
		Meep meep = new Meep();
		Pair<String, String> resp = meep.getResponse("hello");
		assertEquals("Hello there!", resp.getFirst());
		assertEquals("HelloCommand", resp.getSecond());
	}
}
