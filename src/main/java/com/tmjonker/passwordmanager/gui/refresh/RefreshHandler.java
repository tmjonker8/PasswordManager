package com.tmjonker.passwordmanager.gui.refresh;

import com.tmjonker.passwordmanager.credentials.CredentialHandler;
import com.tmjonker.passwordmanager.credentials.Type;
import com.tmjonker.passwordmanager.gui.dialog.ExceptionDialog;
import com.tmjonker.passwordmanager.gui.toggle.ToggleHandler;
import com.tmjonker.passwordmanager.gui.window.MainWindow;
import com.tmjonker.passwordmanager.users.User;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class RefreshHandler {

    public static void refresh(MainWindow mainWindow) {

        // determines which tree item is selected and refreshes the list of credentials associated with it.
        ToggleHandler toggleHandler = mainWindow.getToggleHandler();
        Type selected = mainWindow.getSideBar().getTreeBar().getSelected();
        User verifiedUser = mainWindow.getVerifiedUser();

        try {
            CredentialHandler credentialHandler = new CredentialHandler();
            //updates table to reflect the updates to the credential list.
            mainWindow.getInnerContainer().setTableContent(credentialHandler
                    .generateObservableList(toggleHandler.isShown(), selected, verifiedUser));
        } catch (IOException | GeneralSecurityException ex) {
            new ExceptionDialog(ex);
        }
    }
}
