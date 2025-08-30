package meep.ui;

import meep.tool.Parser;

/**
 * Entry point of the Meep CLI task assistant.
 */
public class Meep {
     /**
      * Starts the Meep application event loop, reading commands and dispatching
      * them to the parser until the user enters "bye".
      *
      * @param args standard program arguments (unused)
      */
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

