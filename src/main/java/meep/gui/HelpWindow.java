package meep.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * A scrollable help window that lists available commands and their syntaxes as
 * vertically stacked labels. Content is sourced from Meep's "help" response and
 * rendered via FXML and CSS.
 */
public class HelpWindow {
    private final Stage stage = new Stage();

    @FXML
    private ScrollPane scroll;
    @FXML
    private VBox content;
    @FXML
    private Label header;

    /**
     * Builds the help window using the raw help text from Meep.
     *
     * @param helpText
     *            response produced by Meep for the "help" command
     */
    public HelpWindow(String helpText) {
        stage.setTitle("Meep Help");

        try {
            FXMLLoader fxmlLoader =
                    new FXMLLoader(HelpWindow.class.getResource("/view/HelpWindow.fxml"));
            fxmlLoader.setController(this);
            ScrollPane root = fxmlLoader.load();
            Scene scene = new Scene(root, 520, 600);
            stage.setScene(scene);
            stage.initModality(Modality.NONE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load HelpWindow.fxml", e);
        }

        header.setText("Meep Commands");
        populateFromHelp(helpText == null ? "" : helpText);
    }

    private void populateFromHelp(String helpText) {
        String pendingTitle = null;
        StringBuilder pendingDesc = new StringBuilder();
        for (String rawLine : helpText.split("\n")) {
            String line = rawLine.strip();
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith("Here are the list of commands")) {
                continue;
            }

            if (line.endsWith(":")) {
                if (pendingTitle != null) {
                    addEntry(pendingTitle, pendingDesc.toString().trim());
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
            addEntry(pendingTitle, pendingDesc.toString().trim());
        }
    }

    private void addEntry(String syntax, String description) {
        VBox entry = new VBox(2);
        entry.getStyleClass().add("help-entry");

        Label title = new Label(syntax);
        title.getStyleClass().add("help-entry-title");
        title.setWrapText(true);

        Label desc = new Label(description);
        desc.getStyleClass().add("help-entry-desc");
        desc.setWrapText(true);

        entry.getChildren().addAll(title, desc);
        content.getChildren().add(entry);
    }

    /** Shows the help window. */
    public void show() {
        stage.show();
        stage.toFront();
    }
}
