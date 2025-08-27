package meep.ui;

import meep.tool.Parser;

public class Meep {
    public static void main(String[] args) {
        Ui.printResponse("Hello from Meep!\nWhat can I do for you?");

        String message = "";
        message = Ui.readCommand();
        while (!message.equals("bye")) {
            Parser.parse(message);
            message = Ui.readCommand();
        }
        Ui.printResponse("Bye. Hope to see you again soon!");
    }
}

