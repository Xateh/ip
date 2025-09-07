package meep.gui;

import java.io.IOException;
import java.util.Collections;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's
 * face and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
	@FXML
	private Label dialog;
	@FXML
	private ImageView displayPicture;

	private DialogBox(String text, Image img) {
		try {
			FXMLLoader fxmlLoader =
					new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
			fxmlLoader.setController(this);
			fxmlLoader.setRoot(this);
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		dialog.setText(text);
		displayPicture.setImage(img);
	}

	/**
	 * Flips the dialog box such that the ImageView is on the left and text on the
	 * right.
	 */
	private void flip() {
		ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
		Collections.reverse(tmp);
		getChildren().setAll(tmp);
		setAlignment(Pos.TOP_LEFT);
		dialog.getStyleClass().add("reply-label");
	}

	public static DialogBox getUserDialog(String text, Image img) {
		return new DialogBox(text, img);
	}

	public static DialogBox getMeepDialog(String text, Image img, String type) {
		var db = new DialogBox(text, img);
		db.flip();
		// Apply additional styling based on command type
		db.changeDialogStyle(type);
		return db;
	}

	private void changeDialogStyle(String commandType) {
		switch (commandType) {
			// Adds
			case "AddMessageCommand", "AddTaskCommand" -> dialog.getStyleClass().add("add-label");
			// Mark/unmark
			case "MarkCommand", "UnmarkCommand" -> dialog.getStyleClass().add("marked-label");
			// Deletes and errors
			case "DeleteCommand", "UnknownCommand", "Error" ->
				dialog.getStyleClass().add("delete-label");
			// Neutral/informational commands (no extra styling beyond reply-label)
			case "HelloCommand" -> {
			}
			case "HowAreYouCommand" -> {
			}
			case "ListMessagesCommand" -> {
			}
			case "ListTasksCommand" -> {
			}
			case "SaveCommand" -> {
			}
			case "LoadCommand" -> {
			}
			case "CheckDueCommand" -> {
			}
			case "HelpCommand" -> {
			}
			case "FindCommand" -> {
			}
			default -> {
			}
		}
	}
}
