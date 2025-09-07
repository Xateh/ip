package meep.ui;

import java.util.Scanner;

/**
 * Console I/O helper for Meep.
 *
 * <p>
 * Abstracts reading user input and printing framed responses to standard out.
 */
public class Ui {
	private static Scanner scanner = new Scanner(System.in);

	/**
	 * Replaces the input scanner used by {@link #readCommand()}.
	 *
	 * @param newScanner
	 *            a non-null scanner instance
	 */
	static void setScanner(Scanner newScanner) {
		assert newScanner != null : "scanner must not be null";
		scanner = newScanner;
	}

	/** Reads a single line command from standard input. */
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
