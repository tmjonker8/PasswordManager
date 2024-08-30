package com.tmjonker.passwordmanager.main;

public class Main {

    public static void main(String[] args) {

        FileStructureCreator.generateFileFolders();
        Bridge.main(args);
    }
}
