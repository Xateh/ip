package meep.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import meep.tool.Pair;
import meep.ui.Meep;

/** Controller for the main GUI. */
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

	/**
	 * Creates two dialog boxes, one echoing user input and the other containing
	 * Duke's reply and then appends them to the dialog container. Clears the user
	 * input after processing.
	 */
	@FXML
	private void handleUserInput() {
		String input = userInput.getText();
		Pair<String, String> response = meep.getResponse(input);
		dialogContainer
				.getChildren()
				.addAll(
						DialogBox.getUserDialog(input, userImage),
						DialogBox.getMeepDialog(
								response.getFirst(), meepImage, response.getSecond()));
		userInput.clear();
	}
}
