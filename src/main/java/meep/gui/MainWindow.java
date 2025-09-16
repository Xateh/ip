package meep.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import meep.tool.Pair;
import meep.ui.Meep;

/** Main window controller for the JavaFX GUI. */
public class MainWindow extends AnchorPane {
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private VBox dialogContainer;
	@FXML
	private TextField userInput;
	@FXML
	private Button sendButton;

	private Meep meep;
	private Stage stage;

	private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
	private Image meepImage = new Image(this.getClass().getResourceAsStream("/images/DaMeep.png"));

	@FXML
	public void initialize() {
		scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
	}

	/** Injects the Meep instance */
	public void setMeep(Meep m) {
		meep = m;
	}

	/** Injects the primary stage so we can close the app gracefully. */
	public void setStage(Stage s) {
		this.stage = s;
	}

	/**
	 * Creates two dialog boxes, one echoing user input and the other containing
	 * Duke's reply and then appends them to the dialog container. Clears the user
	 * input after processing.
	 */
	@FXML
	private void handleUserInput() {
		String input = userInput.getText();

		// Special handling for GUI shutdown when ByeCommand is invoked
		if ("bye".equals(input.trim())) {
			// Show user and Meep farewell messages without routing to Parser
			dialogContainer
					.getChildren()
					.addAll(
							DialogBox.getUserDialog(input, userImage),
							DialogBox.getMeepDialog(
									"Bye. Hope to see you again soon!", meepImage, "Goodbye"));
			userInput.clear();
			userInput.setDisable(true);
			sendButton.setDisable(true);
			PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
			delay.setOnFinished(
					e -> {
						try {
							if (stage != null)
								stage.close();
						} finally {
							Platform.exit();
						}
					});
			delay.play();
			return;
		}

		Pair<String, String> response = meep.getResponse(input);
		// If this was the help command, use the bot's help text to populate the GUI
		if ("HelpCommand".equals(response.getSecond())) {
			new HelpWindow(response.getFirst()).show();
		}
		// If ByeCommand came from Parser (e.g., programmatic calls), also close
		if ("ByeCommand".equals(response.getSecond())) {
			userInput.setDisable(true);
			sendButton.setDisable(true);
			PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
			delay.setOnFinished(
					e -> {
						try {
							if (stage != null)
								stage.close();
						} finally {
							Platform.exit();
						}
					});
			delay.play();
		}
		dialogContainer
				.getChildren()
				.addAll(
						DialogBox.getUserDialog(input, userImage),
						DialogBox.getMeepDialog(
								response.getFirst(), meepImage, response.getSecond()));
		userInput.clear();
	}
}
