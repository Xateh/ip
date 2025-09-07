package meep.gui;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import meep.ui.Meep;

/**
 * JavaFX entry-point for Meep using FXML layouts.
 *
 * <p>Loads the main window from FXML and wires the controller with a Meep
 * instance.
 */
public class Main extends Application {

	private Meep duke = new Meep();

	@Override
	public void start(Stage stage) {
		try {
			stage.setMinHeight(220);
			stage.setMinWidth(417);

			FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
			AnchorPane ap = fxmlLoader.load();
			Scene scene = new Scene(ap);

			stage.setScene(scene);
			fxmlLoader.<MainWindow>getController().setMeep(duke); // inject the Meep instance
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
