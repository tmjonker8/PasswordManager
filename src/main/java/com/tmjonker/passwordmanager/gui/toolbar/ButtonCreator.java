package com.tmjonker.passwordmanager.gui.toolbar;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ButtonCreator {

    public static Button generateButton(Image image) {

        Button button = new Button();
        button.setGraphic(new ImageView(image));

        return button;
    }

    public static Button generateOkButton() {

        Button okButton;
        okButton = new Button("Ok");
        okButton.setMinWidth(60);
        return okButton;
    }

    public static Button generateCancelButton() {

        Button cancelButton;
        cancelButton = new Button("Cancel");
        cancelButton.setMinWidth(60);
        return cancelButton;
    }

    public static void changeButtonImage(Button button, Image image) {
        button.setGraphic(new ImageView(image));
    }
}
