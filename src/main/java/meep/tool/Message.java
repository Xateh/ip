package meep.tool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Immutable user message with a timestamp. */
class Message {
	private String message;
	private LocalDateTime time;

	/**
	 * Creates a message with the current timestamp.
	 *
	 * @param message
	 *            content
	 */
	Message(String message) {
		this.message = message;
		this.time = LocalDateTime.now();
	}

	/**
	 * Returns a formatted string representation including timestamp and content.
	 *
	 * @return formatted message string
	 */
	@Override
	public String toString() {
		return "["
				+ time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
				+ "] "
				+ message;
	}
}
