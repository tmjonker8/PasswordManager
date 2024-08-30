package com.tmjonker.passwordmanager.gui.window;

import com.tmjonker.passwordmanager.gui.dialog.ExceptionDialog;
import com.tmjonker.passwordmanager.gui.dialog.LoginDialog;
import com.tmjonker.passwordmanager.gui.dialog.NewUserDialog;
import com.tmjonker.passwordmanager.gui.dialog.SuccessDialog;
import com.tmjonker.passwordmanager.gui.refresh.RefreshHandler;
import com.tmjonker.passwordmanager.gui.sidebar.SideBar;
import com.tmjonker.passwordmanager.gui.toggle.ToggleHandler;
import com.tmjonker.passwordmanager.gui.toolbar.ButtonCreator;
import com.tmjonker.passwordmanager.gui.toolbar.ToolBarHandler;
import com.tmjonker.passwordmanager.users.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.controlsfx.control.StatusBar;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MainWindow implements WindowShell{

    private final String DEFAULT_TITLE = "Password Manager - ";

    private final MenuBar menuBar = new MenuBar();

    private final Menu fileMenu = new Menu("_File");
    private final Menu editMenu = new Menu("_Edit");
    private final Menu accountMenu = new Menu("_Account");
    private final Menu helpMenu = new Menu("_Help");

    private final MenuItem addMenuItem = new MenuItem("A_dd");
    private final MenuItem deleteMenuItem = new MenuItem("_Delete");
    private final MenuItem newAccountItem = new MenuItem("_New Account");
    private final MenuItem logInMenuItem = new MenuItem("Log _In");
    private final MenuItem logOutMenuItem = new MenuItem("_Log Out");
    private final MenuItem exitMenuItem = new MenuItem("E_xit Program");
    private final MenuItem tutorialItem = new MenuItem("_View Tutorial");

    private Button addButton;
    private Button editButton;
    private Button removeButton;
    private Button refreshButton;
    private Button toggleButton;

    private ToggleHandler toggleHandler = new ToggleHandler();

    private final ToolBar toolBar = new ToolBar();
    private final StatusBar statusBar = new StatusBar();
    private SideBar sideBar = new SideBar(this);

    private final VBox topVbox = new VBox();
    private final BorderPane borderPane = new BorderPane();
    private InnerContainer innerContainer;

    private final Stage stage = new Stage();

    private User verifiedUser;

    private boolean loggedIn;

    public MainWindow() {

        prepareStage(generateStructure(1150, 600));
    }

    private Scene generateStructure(int width, int height) {

        innerContainer = new InnerContainer();
        menuBar.getMenus().addAll(fileMenu, editMenu, accountMenu, helpMenu);
        fileMenu.getItems().addAll(exitMenuItem);
        editMenu.getItems().addAll(addMenuItem, deleteMenuItem);
        accountMenu.getItems().addAll(logInMenuItem,newAccountItem,logOutMenuItem);
        helpMenu.getItems().addAll(tutorialItem);

        addMenuItem.setOnAction(e -> ToolBarHandler.onAddButtonClick(this)); //uses same handler as toolbar buttons. functions exactly the same.
        deleteMenuItem.setOnAction(e -> ToolBarHandler.onRemoveButtonClick(this)); //uses same handler as toolbar buttons. functions exactly the same.
        exitMenuItem.setOnAction(e -> exit());
        logInMenuItem.setOnAction(e -> logIn());
        newAccountItem.setOnAction(e -> newAccount());
        logOutMenuItem.setOnAction(e -> logOut());
        tutorialItem.setOnAction(e -> showTutorial());

        topVbox.getChildren().add(menuBar);

        implementToolBar();

        statusBar.setText("");

        borderPane.setTop(topVbox);
        borderPane.setBottom(statusBar);
        borderPane.setLeft(sideBar.getMainBox());
        borderPane.setCenter(innerContainer.getScrollPane());

        return new Scene(borderPane, width, height);
    }

    private void implementToolBar() {

        addButton = ButtonCreator.generateButton(new Image("images/add_24px.png"));
        addButton.setOnAction(e -> ToolBarHandler.onAddButtonClick(this));
        addButton.setOnMouseEntered(e -> setStatusBarText("Add a new credential"));
        addButton.setOnMouseExited(e -> setStatusBarText(""));

        editButton = ButtonCreator.generateButton(new Image("images/edit_file_24px.png"));
        editButton.setOnAction(e -> ToolBarHandler.onEditButtonClick(this));
        editButton.setOnMouseEntered(e -> setStatusBarText("Edit the selected credential"));
        editButton.setOnMouseExited(e -> setStatusBarText(""));

        removeButton = ButtonCreator.generateButton(new Image("images/delete_24px.png"));
        removeButton.setOnAction(e -> ToolBarHandler.onRemoveButtonClick(this));
        removeButton.setOnMouseEntered(e -> setStatusBarText("Remove the selected credential"));
        removeButton.setOnMouseExited(e -> setStatusBarText(""));

        refreshButton = ButtonCreator.generateButton(new Image("images/refresh_24px.png"));
        refreshButton.setOnAction(e -> RefreshHandler.refresh(this));
        refreshButton.setOnMouseEntered(e -> setStatusBarText("Refresh table data"));
        refreshButton.setOnMouseExited(e -> setStatusBarText(""));

        toggleButton = ButtonCreator.generateButton(new Image("images/hide_24px.png"));
        toggleButton.setOnAction(e -> setToggleButton());
        toggleButton.setOnMouseEntered(e -> {
            if (toggleHandler.isShown())
                setStatusBarText("Hide your passwords");
            else
                setStatusBarText("Show your passwords");
        });
        toggleButton.setOnMouseExited(e -> setStatusBarText(""));

        setLoggedInConfig(false);

        toolBar.getItems().addAll(addButton, editButton, removeButton, refreshButton, toggleButton);

        topVbox.getChildren().add(toolBar);
    }

    private void setStatusBarText(String text) {

        statusBar.setText(text);
    }

    private void centerStage() {

        stage.centerOnScreen();
    }

    private void prepareStage(Scene scene) {

        stage.setScene(scene);
        stage.getIcons().add(new Image("images/password_16px.png"));
        centerStage();
        stage.show();
        stage.setResizable(true);
        stage.setOnCloseRequest(e -> onStageCloseRequest());
    }

    private void setStageTitle(String text) {

        stage.setTitle(DEFAULT_TITLE + text);
    }

    private void logIn() {

        LoginDialog loginDialog = new LoginDialog();

        if (loginDialog.getLoggedIn()) {
            setVerifiedUser(loginDialog.getVerifiedUser());
            sideBar.generateSideBar();
            setLoggedInConfig(true);

            new SuccessDialog(verifiedUser.getUsername() + " has been logged in.", "Success");
        }
    }

    private void setLoggedInConfig(boolean result) {

        loggedIn = result;

        editMenu.setDisable(!result);
        logInMenuItem.setDisable(result);
        newAccountItem.setDisable(result);
        logOutMenuItem.setDisable(!result);

        editButton.setDisable(!result);
        toggleButton.setDisable(!result);
        removeButton.setDisable(!result);
        addButton.setDisable(!result);
        refreshButton.setDisable(!result);

        if (result) {
            Thread focusGetter = new Thread(new FocusGetter());
            focusGetter.start(); // starts thread that checks for selected table entry to enable/disable edit & remove buttons.
            setStageTitle(verifiedUser.getUsername());
        } else
            setStageTitle("");
    }

    private void newAccount() {

        NewUserDialog newUserDialog = new NewUserDialog();

        if (newUserDialog.newUserCreated()) {
            setVerifiedUser(newUserDialog.getVerifiedUser());
            sideBar.generateSideBar();
            setLoggedInConfig(true);

            new SuccessDialog("An account for " + verifiedUser.getUsername() + " has been created.",
                    "Success");
        }
    }

    private void setToggleButton() {

        toggleHandler.toggle(this);

        if (toggleHandler.isShown()) {
            ButtonCreator.changeButtonImage(toggleButton, new Image("images/hide_24px.png"));
        } else {
            ButtonCreator.changeButtonImage(toggleButton, new Image("images/eye_24px.png"));
        }
    }

    private void logOut() {

        verifiedUser = null;
        sideBar = new SideBar(this);
        borderPane.setLeft(sideBar.getMainBox());
        innerContainer.setTableContent(null);
        setLoggedInConfig(false);
    }

    private void showTutorial() {

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        webEngine.load(getClass().getResource("/help.html").toString());

        Scene webScene = new Scene(webView);
        Stage webStage = new Stage();
        webStage.setScene(webScene);
        webStage.setTitle("Password Manager Tutorial");
        webStage.show();
    }

    public User getVerifiedUser() {

        return verifiedUser;
    }

    public InnerContainer getInnerContainer() {

        return innerContainer;
    }

    public SideBar getSideBar() {

        return sideBar;
    }

    public ToggleHandler getToggleHandler() {
        return toggleHandler;
    }

    protected void exit() {
        System.exit(0);
    }

    public void setVerifiedUser(User user) {

        verifiedUser = user;
    }

    @Override
    public void onStageCloseRequest() {

        logOut();
        System.exit(0);
    }

    /**
     * Runnable class that is created when focusGetter thread is started.  Checks to see if a row is selected on the
     * table in InnerContainer.  If a row is selected, then the edit and remove buttons are enabled.  If no button is
     * selected, then those buttons are disabled.
     */
    public class FocusGetter implements Runnable {

        @Override
        public void run() {
            while (loggedIn) {
                if (innerContainer.getSelectedRow() == null) {
                    editButton.setDisable(true);
                    removeButton.setDisable(true);
                    deleteMenuItem.setDisable(true);
                } else {
                    editButton.setDisable(false);
                    removeButton.setDisable(false);
                    deleteMenuItem.setDisable(false);
                }
            }
        }
    }
}
