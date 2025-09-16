package meep.gui;

import javafx.application.Application;

/**
 * Thin launcher to invoke {@link Main} to work around possible classpath issues
 * when packaging.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
