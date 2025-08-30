package meep.tool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single user message with the timestamp when it was received.
 */
class Message {
    private String message;
    private LocalDateTime time;

     /**
      * Creates a new Message, capturing the current time.
      *
      * @param message the textual content
      */
    public Message(String message) {
        this.message = message;
        this.time = LocalDateTime.now();
    }

     /**
      * Returns a formatted representation including the timestamp and content.
      *
      * @return formatted message string
      */
    @Override
    public String toString() {
        return "[" + time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] " + message;
    }
}