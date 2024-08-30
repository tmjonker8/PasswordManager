package com.tmjonker.passwordmanager.gui.dialog;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SuccessDialog {

    public SuccessDialog(String message, String title) {

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle(title);
        success.setHeaderText(title);
        success.setContentText(message);
        success.setGraphic(new ImageView(new Image("images/thumbs_up_48px.png")));

        //Stage stage = (Stage) success.getDialogPane().getScene().getWindow();
        //stage.getIcons().add(new Image("checked_16px.png"));

        success.showAndWait();
    }
}
