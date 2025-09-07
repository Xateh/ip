package meep.gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A simple scrollable help window that lists all available commands and their
 * syntaxes as vertically stacked labels. Content is sourced from Meep's
 * response to the "help" command.
 */
public class HelpWindow {
	private final Stage stage = new Stage();

	/**
	 * Builds the help window using the raw help text from Meep.
	 *
	 * @param helpText response produced by Meep for the "help" command
	 */
	public HelpWindow(String helpText) {
		stage.setTitle("Meep Help");

		ScrollPane scroll = new ScrollPane();
		scroll.setFitToWidth(true);

		VBox content = new VBox(10);
		content.setPadding(new Insets(12));

		Label header = new Label("Meep Commands");
		header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		header.setWrapText(true);
		content.getChildren().add(header);

		// Parse the help text into command blocks of the form:
		// <command>:\n\t<description>
		String pendingTitle = null;
		StringBuilder pendingDesc = new StringBuilder();
		for (String rawLine : helpText.split("\n")) {
			String line = rawLine.strip();
			if (line.isEmpty()) {
				continue;
			}
			// Skip common header line if present
			if (line.startsWith("Here are the list of commands")) {
				continue;
			}

			if (line.endsWith(":")) {
				// Flush previous if any
				if (pendingTitle != null) {
					add(content, pendingTitle, pendingDesc.toString().trim());
					pendingDesc.setLength(0);
				}
				pendingTitle = line.substring(0, line.length() - 1).trim();
			} else {
				if (pendingDesc.length() > 0) {
					pendingDesc.append(' ');
				}
				pendingDesc.append(line.replaceFirst("^[\\t ]+", ""));
			}
		}
		if (pendingTitle != null) {
			add(content, pendingTitle, pendingDesc.toString().trim());
		}

		scroll.setContent(content);

		Scene scene = new Scene(scroll, 520, 600);
		stage.setScene(scene);
		stage.initModality(Modality.NONE);
	}

	private static void add(VBox box, String syntax, String description) {
		Label label = new Label(syntax + "\n  " + description);
		label.setWrapText(true);
		label.setStyle(
				"-fx-padding: 6px; -fx-background-color: #f6f8fa; -fx-background-radius: 6px;");
		box.getChildren().add(label);
	}

	/** Shows the help window. */
	public void show() {
		stage.show();
		stage.toFront();
	}
}
