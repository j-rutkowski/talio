package client.utils;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class PasswordDialog {

    private final Dialog<String> dialog;
    private final GridPane grid;
    private final PasswordField passwordField;
    private final Label label;

    /**
     * Creates a custom dialog for entering a password
     *
     * @param title the title of the dialog
     * @param headerText the header text of the dialog
     * @param contentText the content text of the dialog
     * @param promptText the prompt text of the dialog
     */
    public PasswordDialog(String title,
                          String headerText,
                          String contentText,
                          String promptText) {
        // Create the custom dialog.
        dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.setContentText(contentText);

        // Set the button types.
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        passwordField = new PasswordField();
        passwordField.setPromptText(promptText);

        label = new Label();


        grid.add(new Label(promptText + ":"), 0, 0);
        grid.add(passwordField, 1, 0);
        grid.add(label, 0, 1, 2, 1);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the password field by default.
        Platform.runLater(passwordField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return passwordField.getText();
            }
            return null;
        });
    }

    /**
     * @param message the message to display
     */
    public void invalidate(String message) {
        label.setText(message);
        label.setStyle("-fx-text-fill: red");
    }

    /**
     * @return the optional containing password entered by the user
     */
    public Optional<String> showAndWait() {
        return dialog.showAndWait();
    }

    /**
     * @return the dialog
     */
    public Dialog<String> getDialog() {
        return dialog;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return passwordField.getText();
    }

    /**
     * @return the ok button
     */
    public Button getOkButton() {
        return (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
    }

}
