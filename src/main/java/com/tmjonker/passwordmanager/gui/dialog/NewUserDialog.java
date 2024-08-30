package com.tmjonker.passwordmanager.gui.dialog;

import com.tmjonker.passwordmanager.users.User;
import com.tmjonker.passwordmanager.users.UserHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

public class NewUserDialog {

    private final GridPane gridPane;
    private final Dialog<User> inputDialog;
    private final ButtonType addButtonType;
    private UserHandler userHandler;
    private boolean successful = false;
    private User verifiedUser;

    public NewUserDialog() {

        inputDialog = new Dialog<>();

        try {
            userHandler = new UserHandler();
        } catch (IOException | GeneralSecurityException ex) {
            new ExceptionDialog(ex);
        }

        Stage stage = (Stage) inputDialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("images/password_16px.png"));

        inputDialog.setTitle("Create new user");
        inputDialog.setHeaderText("Create new user");

        addButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        inputDialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        Button okButton = (Button) inputDialog.getDialogPane().lookupButton(addButtonType);
        okButton.setDisable(true);

        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        PasswordField password1Field = new PasswordField();
        password1Field.setPromptText("Password");

        PasswordField password2Field = new PasswordField();
        password2Field.setPromptText("Verify Password");

        gridPane.add(new Label("Username:"), 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(new Label("Password:"), 0, 1);
        gridPane.add(password1Field, 1, 1);
        gridPane.add(new Label("Verify Password:"), 0, 2);
        gridPane.add(password2Field, 1, 2);

        inputDialog.getDialogPane().setContent(gridPane);

        okButton.addEventFilter(ActionEvent.ACTION, ae -> {
            if (password1Field.getText().trim().isEmpty() || password2Field.getText().trim().isEmpty()) {
                ae.consume();
                new ErrorDialog("Password fields can't be empty.", "Error");
            } else if (!password1Field.getText().equals(password2Field.getText())) {
                ae.consume();
                new ErrorDialog("Passwords do not match.", "Error");
            } else if(usernameField.getText().contains(" ")) {
                ae.consume();
                new ErrorDialog("Username can't contain a space.", "Error");
            } else if(userHandler.checkUsernameExists(usernameField.getText().trim())) {
                ae.consume();
                new ErrorDialog("Username already exists.", "Error");
            }
        });

        inputDialog.setResultConverter(ok -> {
            if (ok == addButtonType) {
                try {
                    return userHandler.createUser(usernameField.getText().trim().toLowerCase(),
                            password1Field.getText().getBytes());
                } catch (IOException | GeneralSecurityException ex) {
                    new ExceptionDialog(ex);
                }
            }
            return null;
        });

        Optional<User> result = inputDialog.showAndWait();
        result.ifPresent(user -> setVerifiedUser(user));
    }

    public void setVerifiedUser(User user) {

        successful = true;
        try {
            userHandler.storeUser(user);
        } catch (IOException ex) {
            new ExceptionDialog(ex);
        }
        verifiedUser = user;
    }

    public boolean newUserCreated() {

        return successful;
    }

    public User getVerifiedUser() {

        return verifiedUser;
    }
}
