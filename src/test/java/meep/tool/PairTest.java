package meep.tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class PairTest {
	@Test
	void gettersReturnValues() {
		Pair<Integer, String> p = new Pair<>(42, "answer");
		assertEquals(42, p.getFirst());
		assertEquals("answer", p.getSecond());
	}

	@Test
	void equalsAndHashCode() {
		Pair<Integer, String> p1 = new Pair<>(1, "a");
		Pair<Integer, String> p2 = new Pair<>(1, "a");
		Pair<Integer, String> p3 = new Pair<>(2, "b");
		assertEquals(p1, p2);
		assertEquals(p1.hashCode(), p2.hashCode());
		assertNotEquals(p1, p3);
	}
}
