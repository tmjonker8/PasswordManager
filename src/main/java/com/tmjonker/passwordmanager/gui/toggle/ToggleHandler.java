package com.tmjonker.passwordmanager.gui.toggle;

import com.tmjonker.passwordmanager.credentials.Credential;
import com.tmjonker.passwordmanager.credentials.CredentialHandler;
import com.tmjonker.passwordmanager.credentials.Type;
import com.tmjonker.passwordmanager.gui.dialog.ExceptionDialog;
import com.tmjonker.passwordmanager.gui.window.MainWindow;
import com.tmjonker.passwordmanager.users.User;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Map;

public class ToggleHandler {

    private boolean shown = false;

    public void toggle (MainWindow mainWindow) {

        Type selected = mainWindow.getSideBar().getTreeBar().getSelected();
        CredentialHandler credentialHandler;

        if (shown)
            shown = false;
        else
            shown = true;

        try {
            credentialHandler = new CredentialHandler();

            mainWindow.getInnerContainer().setTableContent(credentialHandler
                    .generateObservableList(shown, selected, mainWindow.getVerifiedUser()));

            mainWindow.getInnerContainer().setShown(shown);

        } catch (Exception ex) {
            new ExceptionDialog(ex);
        }
    }

    public void setShown(boolean hide) {
        shown = false;
    }

    public boolean isShown() {
        return shown;
    }
}
