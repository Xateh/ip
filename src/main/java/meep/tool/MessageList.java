package meep.tool;

import java.util.ArrayList;

/**
 * In-memory list of {@link Message} with simple operations and iteration
 * helpers.
 */
class MessageList {
	private ArrayList<Message> messages = new ArrayList<>();

	/**
	 * Adds a message by content, creating a {@link Message} with current timestamp.
	 *
	 * @param message
	 *            content
	 * @return the string representation of the added message
	 */
	public String addMessage(String message) {
	assert message != null : "message must not be null";
		return addMessage(new Message(message));
	}

	/**
	 * Adds an existing message object to the list.
	 *
	 * @param message
	 *            message instance
	 * @return the string representation of the added message
	 */
	public String addMessage(Message message) {
	assert message != null : "message must not be null";
		messages.add(message);
		return message.toString();
	}

	/**
	 * Removes and returns the message at an index.
	 *
	 * @param index
	 *            0-based index
	 * @return removed message
	 */
	public Message removeMessage(int index) {
		return messages.remove(index);
	}

	/**
	 * Clears all messages.
	 *
	 * @return true after clearing
	 */
	public boolean clearMessages() {
		messages.clear();
		return true;
	}

	/**
	 * Returns the number of messages.
	 *
	 * @return size of the list
	 */
	public int size() {
		return messages.size();
	}

	/**
	 * Iterates over messages with a simple action.
	 *
	 * @param action
	 *            callback invoked for each message
	 */
	public void iterateMessages(MessageAction action) {
	assert action != null : "action must not be null";
		for (Message message : messages) {
			action.apply(message);
		}
	}

	/**
	 * Iterates over messages with index and message.
	 *
	 * @param action
	 *            callback invoked for each (message, index)
	 */
	public void iterateMessages(IndexMessageAction action) {
	assert action != null : "action must not be null";
		for (int i = 0; i < messages.size(); i++) {
			action.apply(messages.get(i), i);
		}
	}

	@FunctionalInterface
	interface MessageAction {
		/**
		 * Applies an action to a message.
		 *
		 * @param message
		 *            the message
		 */
		void apply(Message message);
	}

	@FunctionalInterface
	interface IndexMessageAction {
		/**
		 * Applies an action to a message with its position.
		 *
		 * @param message
		 *            the message
		 * @param index
		 *            0-based index
		 */
		void apply(Message message, int index);
	}
}
