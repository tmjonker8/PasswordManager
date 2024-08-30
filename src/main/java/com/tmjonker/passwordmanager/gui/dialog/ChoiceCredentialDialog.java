package com.tmjonker.passwordmanager.gui.dialog;

import com.tmjonker.passwordmanager.credentials.*;
import com.tmjonker.passwordmanager.generator.PasswordGenerator;
import com.tmjonker.passwordmanager.gui.refresh.RefreshHandler;
import com.tmjonker.passwordmanager.gui.window.MainWindow;
import com.tmjonker.passwordmanager.users.User;
import com.tmjonker.passwordmanager.users.UserHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChoiceCredentialDialog {

    private final MainWindow mainWindow;

    private final Choice choice;
    private Type type;

    private UserHandler userHandler;
    private CredentialHandler credentialHandler;

    private final User verifiedUser;
    private Credential credential;

    private Dialog<Credential> inputDialog;
    private Optional<Credential> result;

    private GridPane gridPane;
    private final TextField uniqueField = new TextField();
    private final Label uniqueLabel = new Label();
    private final TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private TextField passwordTextField;
    private ButtonType choiceButtonType;
    private final Button toggleButton = new Button("Show");
    private final Hyperlink generateLink = new Hyperlink("Generate Password");

    private boolean hide = true; // to determine if user wants password to be visible or not.

    public ChoiceCredentialDialog(Choice choice, MainWindow mainWindow) {

        this.choice = choice;
        this.verifiedUser = mainWindow.getVerifiedUser();
        this.mainWindow = mainWindow;

        try {
            userHandler = new UserHandler();
            credentialHandler = new CredentialHandler();
        } catch (IOException | GeneralSecurityException ex) {
            new ExceptionDialog(ex);
        }

        if (choice == Choice.ADD)
            showChoiceDialog();
        else {
            // gets the selected credential from the table.
            credential = mainWindow.getInnerContainer().getSelectedRow();
            type = credential.getType();
            generateNodes();
            populateCredentialData();
            editResult();
        }
    }

    private void showChoiceDialog() {

        List<Type> typeList = new ArrayList<>();
        typeList.add(Type.WEBSITE);
        typeList.add(Type.APPLICATION);
        typeList.add(Type.GAME);

        ChoiceDialog<Type> choiceDialog = new ChoiceDialog<>(Type.WEBSITE, typeList);

        Stage stage = (Stage) choiceDialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("images/password_16px.png"));

        choiceDialog.setTitle("Select a type");
        choiceDialog.setHeaderText("What kind of password do you want to add?");
        choiceDialog.setGraphic(new ImageView(new Image("images/question_mark_48px.png")));

        Optional<Type> result = choiceDialog.showAndWait();

        result.ifPresent(type -> {
            this.type = type;
            generateNodes();
            addResult();
        });
    }

    private void addResult() {

        inputDialog.setResultConverter(inputButton -> {
            String password;
            if (passwordField != null)
                password = passwordField.getText();
            else
                password = passwordTextField.getText();

            if (inputButton == choiceButtonType) {
                try {
                    switch (type) {
                        case WEBSITE:
                                    return new WebsiteCredential(uniqueField.getText().trim(),
                                            usernameField.getText().trim(), password);
                        case APPLICATION:
                                    return new ApplicationCredential(uniqueField.getText().trim(),
                                            usernameField.getText().trim(), password);
                        case GAME:
                                    return new GameCredential(uniqueField.getText().trim(),
                                            usernameField.getText().trim(), password);
                    }
                } catch (URISyntaxException ex) {
                    new ExceptionDialog(ex);
                }
            }
            return null;
        });

        result = inputDialog.showAndWait();

        result.ifPresent(wc -> {
            try {
                credentialHandler.finalizeCredential(wc);
                userHandler.storeCredential(verifiedUser, wc);
                RefreshHandler.refresh(mainWindow); // updates table to reflect addition of credential.
            } catch (IOException | GeneralSecurityException ex) {
                new ExceptionDialog(ex);
            }
        });
    }

    private void editResult() {

        inputDialog.setResultConverter(inputButton -> {
            if (inputButton == choiceButtonType) {
                String password;
                if (passwordField != null)
                    password = passwordField.getText();
                else
                    password = passwordTextField.getText();

                credential.setUsername(usernameField.getText().trim());
                credential.setPassword(password.getBytes());

                try {
                    credentialHandler.encryptCredential(credential);
                } catch (GeneralSecurityException | IOException ex) {
                    new ExceptionDialog(ex);
                }

                switch (type) {
                    case WEBSITE:
                                try {
                                    ((WebsiteCredential) credential).setUrl(uniqueField.getText());
                                } catch (URISyntaxException ex) {
                                    new ExceptionDialog(ex);
                                }
                                break;
                    case APPLICATION:
                                ((ApplicationCredential) credential).setDisplay(uniqueField.getText());
                                break;
                    case GAME:
                                ((GameCredential) credential).setDisplay(uniqueField.getText());
                                break;
                }

                return credential;
            }
            return null;
        });

        result = inputDialog.showAndWait();
        result.ifPresent(cred -> {
            try {
                userHandler.removeCredential(verifiedUser, credential);
                userHandler.storeCredential(verifiedUser, cred);
                RefreshHandler.refresh(mainWindow); //updates table to reflrect edited credential.
            } catch (IOException | GeneralSecurityException ex) {
                new ExceptionDialog(ex);
            }
        });
    }

    private void generateNodes() {

        inputDialog = new Dialog<>();

        Stage stage = (Stage) inputDialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("images/password_16px.png"));

        if (choice == Choice.ADD) {
            inputDialog.setTitle("Add a new " + type);
            inputDialog.setHeaderText("Add a new " + type);
            choiceButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        } else {
            inputDialog.setTitle("Edit existing credential");
            inputDialog.setHeaderText("Edit existing credential");
            choiceButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        }
        inputDialog.getDialogPane().getButtonTypes().addAll(choiceButtonType, ButtonType.CANCEL);

        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        switch (type) {
            case WEBSITE:
                uniqueField.setPromptText("URL");
                uniqueLabel.setText("URL:");
                inputDialog.setGraphic(new ImageView(new Image("images/website_48px.png")));
                break;
            case APPLICATION:
                uniqueField.setPromptText("Application");
                uniqueLabel.setText("Application:");
                inputDialog.setGraphic(new ImageView(new Image("images/application_shield_48px.png")));
                break;
            case GAME:
                uniqueField.setPromptText("Game");
                uniqueLabel.setText("Game:");
                inputDialog.setGraphic(new ImageView(new Image("images/game_controller_48px.png")));
                break;
        }

        usernameField.setPromptText("Username");
        passwordField.setPromptText("Password");
        toggleButton.setOnAction(e -> setTogglePassword());
        generateLink.setOnAction(e ->  {
            if (passwordTextField == null)
                passwordField.setText(PasswordGenerator.generatePassword());
            else
                passwordTextField.setText(PasswordGenerator.generatePassword());
        });

        gridPane.add(uniqueLabel, 0, 0);
        gridPane.add(uniqueField, 1, 0);
        gridPane.add(new Label("Username:"), 0, 1);
        gridPane.add(usernameField, 1, 1);
        gridPane.add(new Label("Password:"), 0, 2);
        gridPane.add(passwordField, 1, 2);
        gridPane.add(toggleButton, 2, 2);
        gridPane.add(generateLink, 1, 3);
        inputDialog.getDialogPane().setContent(gridPane);

        RowConstraints defaultRow = new RowConstraints();
        RowConstraints linkRow = new RowConstraints();
        linkRow.setMaxHeight(5);
        gridPane.getRowConstraints().addAll(defaultRow, defaultRow, defaultRow, linkRow);

        Button choiceButton = (Button) inputDialog.getDialogPane().lookupButton(choiceButtonType);
        choiceButton.addEventFilter(ActionEvent.ACTION, ae -> {
            String password;
            if (passwordField != null)
                password = passwordField.getText();
            else
                password = passwordTextField.getText();

            if (uniqueField.getText().trim().isEmpty()) {
                new ErrorDialog(uniqueField.getPromptText() + " field can't be empty.", "Error");
                ae.consume();
            } else if (usernameField.getText().trim().isEmpty()) {
                new ErrorDialog("Username field can't empty.", "Error");
                ae.consume();
            } else if (password.isEmpty()) {
                new ErrorDialog("Password field can't be empty.", "Error");
                ae.consume();
            } else if (password.contains(" ")) {
                new ErrorDialog("Password cannot contain any spaces", "Error");
                ae.consume();
            }
        });
    }

    //Determines what type of credential is selected and fills in the text fields with the credential's data.
    private void populateCredentialData() {

        usernameField.setText(credential.getUsername());

        if (passwordField == null)
            passwordTextField.setText(credential.getDecryptedPassword());
        else
            passwordField.setText(credential.getDecryptedPassword());

        switch (type) {
            case WEBSITE:
                    uniqueField.setText(((WebsiteCredential)credential).getUrl().toString());
                    break;
            case APPLICATION:
                    uniqueField.setText(((ApplicationCredential)credential).getDisplay());
                    break;
            case GAME:
                    uniqueField.setText(((GameCredential)credential).getDisplay());
                    break;
        }
    }

    private void setTogglePassword() {
        if (!hide) {
            passwordField = new PasswordField();
            passwordField.setText(passwordTextField.getText());
            passwordField.setPromptText("Password");
            gridPane.add(passwordField, 1, 2);
            passwordTextField = null;
            toggleButton.setText("Show");
            hide = true;
        } else {
            passwordTextField = new TextField(passwordField.getText());
            passwordTextField.setPromptText("Password");
            gridPane.add(passwordTextField, 1, 2);
            passwordField = null;
            toggleButton.setText("Hide");
            hide = false;
        }
    }
}
