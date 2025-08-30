package meep.tool;

import java.util.ArrayList;

/**
 * In-memory collection for Message objects with formatted add behavior.
 */
class MessageList {
    private ArrayList<Message> messages = new ArrayList<>();

     /**
      * Creates and adds a Message from a raw string, returning the formatted value.
      *
      * @param message raw text content
      * @return formatted message string
      */
    public String addMessage(String message) {
        return addMessage(new Message(message));
    }

     /**
      * Adds a Message instance to the list and returns its formatted string.
      *
      * @param message a Message instance
      * @return formatted message string
      */
    public String addMessage(Message message) {
        messages.add(message);
        return message.toString();
    }

     /**
      * Removes and returns the message at the given index.
      *
      * @param index zero-based index to remove
      * @return the removed Message
      */
    public Message removeMessage(int index) {
        return messages.remove(index);
    }

     /**
      * Clears all messages from the list.
      *
      * @return always true after clear
      */
    public boolean clearMessages() {
        messages.clear();
        return true;
    }

     /**
      * Returns the number of messages stored.
      *
      * @return size of the message list
      */
    public int size() {
        return messages.size();
    }

     /**
      * Iterates through messages invoking the provided action for each item.
      *
      * @param action callback to apply to each message
      */
    public void iterateMessages(MessageAction action) {
        for (Message message : messages) {
            action.apply(message);
        }
    }

     /**
      * Iterates through messages invoking the provided action with item and index.
      *
      * @param action callback to apply to each message with index
      */
    public void iterateMessages(IndexMessageAction action) {
        for (int i = 0; i < messages.size(); i++) {
            action.apply(messages.get(i), i);
        }
    }

    @FunctionalInterface
    interface MessageAction {
        void apply(Message message);
    }

    @FunctionalInterface
    interface IndexMessageAction {
        void apply(Message message, int index);
    }
}