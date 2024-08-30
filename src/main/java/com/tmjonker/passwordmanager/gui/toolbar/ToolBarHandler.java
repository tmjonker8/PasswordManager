package com.tmjonker.passwordmanager.gui.toolbar;

import com.tmjonker.passwordmanager.gui.dialog.Choice;
import com.tmjonker.passwordmanager.gui.dialog.ChoiceCredentialDialog;
import com.tmjonker.passwordmanager.gui.dialog.RemoveCredentialDialog;
import com.tmjonker.passwordmanager.gui.window.MainWindow;
import com.tmjonker.passwordmanager.users.User;

public class ToolBarHandler {

    public static void onAddButtonClick(MainWindow mainWindow) {

        new ChoiceCredentialDialog(Choice.ADD, mainWindow);
    }

    public static void onEditButtonClick(MainWindow mainWindow) {

        new ChoiceCredentialDialog(Choice.EDIT, mainWindow);
    }

    public static void onRemoveButtonClick(MainWindow mainWindow) {

        new RemoveCredentialDialog(mainWindow);
    }
}
