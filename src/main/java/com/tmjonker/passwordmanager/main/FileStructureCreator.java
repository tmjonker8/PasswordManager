package com.tmjonker.passwordmanager.main;

import java.io.File;

public class FileStructureCreator {

    private static final String CONFIG_DIRECTORY = System.getProperty("user.dir") + "/config";
    private static final String USER_DIRECTORY = System.getProperty("user.dir") + "/user";
    private static final File CONFIG_FILE = new File(CONFIG_DIRECTORY);
    private static final File USER_FILE = new File(USER_DIRECTORY);

    public static void generateFileFolders() {

        if (!CONFIG_FILE.exists()) {
            if (new File(CONFIG_DIRECTORY).mkdir())
                System.out.println("config directory created.");
            else
                System.out.println("config directory was not created");
        }

        if (!USER_FILE.exists()) {
            if (new File(USER_DIRECTORY).mkdir())
                System.out.println("user directory was created.");
            else
                System.out.println("user directory could not be created.");
        }
    }
}
