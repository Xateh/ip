package meep.tool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Immutable user message with a timestamp.
 *
 * <p>
 * Instances capture the message text and the creation time. The text must be
 * non-null.
 */
class Message {
	private final String message;
	private final LocalDateTime time;

	/**
	 * Creates a message with the current timestamp.
	 *
	 * @param message
	 *            content
	 */
	Message(String message) {
	assert message != null : "Message content must not be null";
		this.message = message;
		this.time = LocalDateTime.now();
	}

	/**
	 * Returns a formatted string representation including timestamp and content.
	 *
	 * @return formatted "[yyyy-MM-dd HH:mm:ss] <text>" string
	 */
	@Override
	public String toString() {
		return "["
				+ time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
				+ "] "
				+ message;
	}
}
