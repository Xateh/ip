package meep.ui;

import java.util.Scanner;

/** Simple console I/O helper for Meep. */
public class Ui {
	private static Scanner scanner = new Scanner(System.in);

	static void setScanner(Scanner newScanner) {
	assert newScanner != null : "scanner must not be null";
		scanner = newScanner;
	}

	/**
	 * Reads a single line command from standard input.
	 *
	 * @return the raw command line
	 */
	public static String readCommand() {
	assert scanner != null : "scanner must be initialized";
		String command = scanner.nextLine();
		return command;
	}

	/**
	 * Prints a response message framed by a horizontal rule.
	 *
	 * @param response
	 *            content to print
	 */
	public static void printResponse(String response) {
	assert response != null : "response must not be null";
		System.out.println("-".repeat(50));
		System.out.println(response);
		System.out.println("-".repeat(50));
	}
}
