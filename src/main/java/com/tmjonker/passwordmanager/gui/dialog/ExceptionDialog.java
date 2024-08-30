package com.tmjonker.passwordmanager.gui.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionDialog {

    public ExceptionDialog(Exception ex) {

        Alert exception = new Alert(Alert.AlertType.ERROR);
        exception.setTitle("Error");
        exception.setHeaderText("Exception");
        exception.setContentText("There has been an error.");

        Stage stage = (Stage) exception.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("images/error_16px.png"));

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");
        TextArea textArea = new TextArea(exceptionText);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane gridPane = new GridPane();
        gridPane.add(label, 0, 0);
        gridPane.add(textArea, 0, 1);

        exception.getDialogPane().setExpandableContent(gridPane);
        exception.getDialogPane().setExpanded(true);

        exception.showAndWait();
    }
}
