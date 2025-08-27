package meep.tool;

import java.util.ArrayList;

class MessageList {
    private ArrayList<Message> messages = new ArrayList<>();

    public String addMessage(String message) {
        return addMessage(new Message(message));
    }

    public String addMessage(Message message) {
        messages.add(message);
        return message.toString();
    }

    public Message removeMessage(int index) {
        return messages.remove(index);
    }

    public boolean clearMessages() {
        messages.clear();
        return true;
    }

    public int size() {
        return messages.size();
    }

    public void iterateMessages(MessageAction action) {
        for (Message message : messages) {
            action.apply(message);
        }
    }

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