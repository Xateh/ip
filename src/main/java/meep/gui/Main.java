package meep.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import meep.ui.Meep;

import java.io.IOException;

import javafx.fxml.FXMLLoader;

/**
 * A GUI for Duke using FXML.
 */
public class Main extends Application {

    private Meep duke = new Meep();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setMeep(duke);  // inject the Meep instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

