package meep.tool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Message {
    private String message;
    private LocalDateTime time;

    public Message(String message) {
        this.message = message;
        this.time = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "[" + time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] " + message;
    }
}