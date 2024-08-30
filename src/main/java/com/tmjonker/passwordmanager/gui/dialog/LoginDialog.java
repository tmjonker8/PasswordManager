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

public class LoginDialog {

    private final GridPane gridPane;
    private final Dialog<User> inputDialog;
    private final ButtonType addButtonType;
    private UserHandler userHandler;
    private boolean successful = false;
    private User verifiedUser;

    public LoginDialog() {

        inputDialog = new Dialog<>();

        try {
            userHandler = new UserHandler();
        } catch (IOException | GeneralSecurityException ex) {
            new ExceptionDialog(ex);
        }

        Stage stage = (Stage) inputDialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("images/password_16px.png"));

        inputDialog.setTitle("Login");
        inputDialog.setHeaderText("Login");

        addButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        inputDialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        Button loginButton = (Button) inputDialog.getDialogPane().lookupButton(addButtonType);
        loginButton.setDisable(true);

        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        gridPane.add(new Label("Username:"), 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(new Label("Password:"), 0, 1);
        gridPane.add(passwordField, 1, 1);

        inputDialog.getDialogPane().setContent(gridPane);

        loginButton.addEventFilter(ActionEvent.ACTION, ae -> {
            if (passwordField.getText().trim().isEmpty()) {
                ae.consume();
                new ErrorDialog("Password field can't be blank.", "Error");
            }
            if (!verifyInput(usernameField.getText().trim().toLowerCase(), passwordField.getText().getBytes())) {
                ae.consume();
                new ErrorDialog("Your username / password combination has failed.", "Invalid login");
            }

        });

        inputDialog.setResultConverter(okButton -> {
            if (okButton == addButtonType) {
                return new User(usernameField.getText().trim().toLowerCase(), passwordField.getText());
            }
            return null;
        });

        Optional<User> result = inputDialog.showAndWait();
        result.ifPresent(user -> {
            finalizeLogin(user, passwordField.getText().getBytes());
        });
    }


    private boolean verifyInput(String username, byte[] password) {

        try {
            if (userHandler.checkUsernameExists(username)) {
                if (userHandler.validateReturningUser(userHandler.loadUser(username), password)) {
                    return true;
                }
            }
        } catch (IOException | GeneralSecurityException ex) {
            new ExceptionDialog(ex);
            return false;
        }
        return false;
    }

    private void finalizeLogin(User user, byte[] password) {

        try {
            String username = user.getUsername();
            user = userHandler.loadUser(username);
            userHandler.updateEncryption(user, password);
            verifiedUser = user;
            successful = true;
        } catch (IOException | GeneralSecurityException ex) {
            new ExceptionDialog(ex);
        }
    }

    public boolean getLoggedIn() {

        return successful;
    }

    public User getVerifiedUser() {

        return verifiedUser;
    }
}
