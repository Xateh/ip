package meep.ui;

import meep.tool.Parser;

/**
 * Application entry point for Meep. Runs a simple REPL until the user types
 * "bye".
 */
public class Meep {
	/**
	 * Starts the Meep CLI.
	 *
	 * @param args
	 *            ignored
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
