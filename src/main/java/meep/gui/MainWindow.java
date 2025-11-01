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
    // Background image is now applied via CSS on the root; no FXML image nodes
    // needed

    private Meep meep;
    private Stage stage;

    private Image userImage;
    private Image meepHappyImage;
    private Image meepSadImage;
    private Image meepTalkImage;
    private Image meepSmileImage;

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        // Load images with graceful fallbacks
        userImage = loadFirstAvailable("/images/avatar.jpg");
        meepHappyImage = loadFirstAvailable("/images/happy_robot.jpg");
        meepSadImage = loadFirstAvailable("/images/sad_robot.jpg");
        meepTalkImage = loadFirstAvailable("/images/talk_robot.jpg");
        meepSmileImage = loadFirstAvailable("/images/smile_robot.jpg");

        // Background handled by CSS; nothing to wire here
    }

    /**
     * Injects the Meep instance.
     *
     * @param m
     *            the Meep backend used by the GUI
     */
    public void setMeep(Meep m) {
        meep = m;
    }

    /**
     * Injects the primary stage so we can close the app gracefully.
     *
     * @param s
     *            the primary JavaFX stage
     */
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
                                    "Bye. Hope to see you again soon!",
                                    meepImageForType("Goodbye"),
                                    "Goodbye"));
            userInput.clear();
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(
                    e -> {
                        try {
                            if (stage != null) {
                                stage.close();
                            }
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
                            if (stage != null) {
                                stage.close();
                            }
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
                                response.getFirst(),
                                meepImageForType(response.getSecond()),
                                response.getSecond()));
        userInput.clear();
    }

    /** Returns the most suitable Meep avatar image for a given command type. */
    private Image meepImageForType(String commandType) {
        if (commandType == null) {
            return meepHappyImage != null ? meepHappyImage : fallbackMeep();
        }
        switch (commandType) {
            case "DeleteCommand" :
            case "UnknownCommand" :
            case "Error" :
                return coalesce(meepSadImage, fallbackMeep());
            case "HelpCommand" :
                return coalesce(meepTalkImage, fallbackMeep());
            case "MarkCommand" :
            case "UnmarkCommand" :
            case "AddMessageCommand" :
            case "AddTaskCommand" :
            case "SaveCommand" :
            case "LoadCommand" :
                return coalesce(meepSmileImage, fallbackMeep());
            case "Goodbye" :
            case "ByeCommand" :
                return coalesce(meepSmileImage, fallbackMeep());
            default :
                return coalesce(meepHappyImage, fallbackMeep());
        }
    }

    /** Attempts to load the first available resource from the provided paths. */
    private Image loadFirstAvailable(String... resourcePaths) {
        for (String p : resourcePaths) {
            try {
                var stream = this.getClass().getResourceAsStream(p);
                if (stream != null) {
                    return new Image(stream);
                }
            } catch (Exception ignored) {
                // try next
            }
        }
        return null;
    }

    /** Returns DaMeep.png fallback image, guaranteed non-null when available. */
    private Image fallbackMeep() {
        Image img = loadFirstAvailable("/images/DaMeep.png");
        if (img == null) {
            // As an absolute last resort, reuse any available user image
            return userImage;
        }
        return img;
    }

    private Image coalesce(Image preferred, Image fallback) {
        return preferred != null ? preferred : fallback;
    }
}
