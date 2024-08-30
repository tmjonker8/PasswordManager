package com.tmjonker.passwordmanager.gui.dialog;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ErrorDialog {

    public ErrorDialog(String message, String title) {

        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle(title);
        error.setHeaderText(title);
        error.setContentText(message);

        Stage stage = (Stage) error.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("images/error_16px.png"));

        error.showAndWait();
    }
}
