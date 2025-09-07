package meep.ui;

import meep.tool.Command;
import meep.tool.Pair;
import meep.tool.Parser;

/**
 * Application entry point for Meep's console app.
 *
 * <p>
 * Runs a simple REPL until the user types "bye".
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
			Command c = Parser.parse(message);
			if (c != null) {
				String response = c.execute();
				if (response != null && !response.isEmpty()) {
					Ui.printResponse(response);
				}
			}
			message = Ui.readCommand();
		}
		Ui.printResponse("Bye. Hope to see you again soon!");
	}

	/**
	 * Generates a response for a user's chat message.
	 *
	 * @param input
	 *            raw user input
	 * @return pair of (response text, command type)
	 */
	public Pair<String, String> getResponse(String input) {
		assert input != null : "input must not be null";
		try {
			Command c = Parser.parseQuiet(input);
			assert c != null : "Parser should return a command";
			return new Pair<>(c.execute(), c.getClass().getSimpleName());
		} catch (Exception e) {
			return new Pair<>("Error: " + e.getMessage(), "Error");
		}
	}
}
