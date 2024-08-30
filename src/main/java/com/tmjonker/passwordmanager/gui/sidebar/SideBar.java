package com.tmjonker.passwordmanager.gui.sidebar;

import com.tmjonker.passwordmanager.gui.window.MainWindow;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class SideBar {

    private final VBox mainBox = new VBox();
    private final MainWindow mainWindow;
    private final TreeBar treeBar;

    public SideBar(MainWindow mainWindow) {

        this.mainWindow = mainWindow;
         treeBar = new TreeBar(mainWindow);
    }

    public void generateSideBar() {

        Image emblem = new Image("images/security_lock_112px.png");
        ImageView imageView = new ImageView(emblem);
        VBox imageBox = new VBox(10);
        imageBox.getChildren().add(imageView);
        imageBox.setAlignment(Pos.CENTER);
        imageBox.prefHeightProperty().bind(mainBox.heightProperty().multiply(0.5)); //sets imageBox to 1/2 height of mainBox.

        treeBar.display(); // generates TreeView object and all associated TreeItems.

        VBox treeBox = new VBox();
        treeBox.prefHeightProperty().bind(mainBox.heightProperty().multiply(0.5)); //sets treeBox to 1/2 height of mainBox.
        treeBox.getChildren().add(treeBar.getTreeView());
        treeBar.getTreeView().prefHeightProperty().bind(treeBox.heightProperty());

        mainBox.getChildren().addAll(treeBox, imageBox);
        mainBox.setAlignment(Pos.TOP_CENTER);
    }

    public VBox getMainBox() {

        return mainBox;
    }

    public TreeBar getTreeBar() {

        return treeBar;
    }
}
