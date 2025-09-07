package meep.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Minimal JavaFX demo UI for Meep that displays a simple greeting.
 *
 * <p>This class is not wired to the FXML chat UI; it serves as a lightweight
 * example to verify JavaFX setup.
 */
public class Gui extends Application {

	private static final String DEFAULT_FILE_PATH = "meep/example.txt";

	/** Configures and shows the primary JavaFX stage. */
	@Override
	public void start(Stage stage) {
		Label helloWorld = new Label("Hello World!"); // Creating a new Label control
		Scene scene = new Scene(helloWorld); // Setting the scene to be our Label

		stage.setScene(scene); // Setting the stage to show our scene
		stage.show(); // Render the stage.
	}

	public Gui(String filePath) { start(new Stage()); }

	public Gui() { this(DEFAULT_FILE_PATH); }
}
