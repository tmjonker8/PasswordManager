package com.tmjonker.passwordmanager.gui.window;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import com.tmjonker.passwordmanager.credentials.Credential;
import com.tmjonker.passwordmanager.credentials.Type;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class InnerContainer {

    private ScrollPane scrollPane;
    private TableView<Credential> table;
    private final Clipboard clipboard = Clipboard.getSystemClipboard();
    private final ClipboardContent content = new ClipboardContent();
    private boolean shown;

    public InnerContainer() {

        generateInnerContainer();
    }

    private void generateInnerContainer() {

        table = new TableView<>();

        setupRightClickMenu();

        TableColumn<Credential, Type> typeColumn = new TableColumn<>("Type");
        typeColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.19));
        typeColumn.setCellValueFactory(new PropertyValueFactory<Credential, Type>("type"));

        TableColumn<Credential, String> uriColumn = new TableColumn<>("For");
        uriColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.27));
        uriColumn.setCellValueFactory(new PropertyValueFactory<Credential, String>("display"));

        TableColumn<Credential, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.27));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<Credential, String>("username"));

        TableColumn<Credential, String> passwordColumn = new TableColumn<>("Password");
        passwordColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.27));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<Credential, String>("decryptedPassword"));

        table.getColumns().addAll(typeColumn, uriColumn, usernameColumn, passwordColumn);
        table.setFocusTraversable(false);

        scrollPane = new ScrollPane(table);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
    }

    public void setTableContent(ObservableList<Credential> list) {

        table.setItems(list);
        table.refresh();
    }

    public ScrollPane getScrollPane() {

        return scrollPane;
    }

    public Credential getSelectedRow() {

        return table.getSelectionModel().getSelectedItem();
    }

    private void setupRightClickMenu() {

        ContextMenu rightClickMenu = new ContextMenu();
        MenuItem copyPasswordItem = new MenuItem("Copy Password");
        rightClickMenu.getItems().addAll(copyPasswordItem);
        table.setContextMenu(rightClickMenu);

        if (!shown)
            copyPasswordItem.setDisable(true);

        copyPasswordItem.setOnAction(e -> {
            if (getSelectedRow() != null) {
                content.putString(getSelectedRow().getDecryptedPassword());
                clipboard.setContent(content);
            }
        });
    }

    public void setShown(boolean choice) {
        shown = choice;
        setupRightClickMenu();
    }
}
