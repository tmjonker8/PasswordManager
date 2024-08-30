package com.tmjonker.passwordmanager.credentials;

import com.tmjonker.passwordmanager.encryption.StringEncoder;

import java.io.Serializable;

public class GameCredential implements Credential, Serializable {

    private String display;
    private String username;
    private byte[] password;
    private Type type;
    private String identifier;
    private String decryptedPassword = null;
    private String keysetHandleString;

    public GameCredential(String display, String username, String password) {

        this.display = display;
        this.username = username;
        this.password = password.getBytes();
        this.type = Type.GAME;
    }

    @Override
    public void setUsername(String username) {

        this.username = username;
    }

    @Override
    public void setPassword(byte[] password) {

        this.password = password;
    }

    @Override
    public void setType(Type type) {

        this.type = type;
    }

    @Override
    public void setIdentifier(int identifier) {

        this.identifier = "c" + identifier;
    }

    @Override
    public void setDecryptedPassword(String password) {

        if (password != null)
            decryptedPassword = password;
        else decryptedPassword = null;
    }

    @Override
    public void setKeysetHandleString(String keysetHandleString) {
        this.keysetHandleString = keysetHandleString;
    }

    public void setDisplay(String display) {

        this.display = display;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public byte[] getPassword() {
        return password;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getDecryptedPassword() {
        return decryptedPassword;
    }

    @Override
    public String getKeysetHandleString() {
        return keysetHandleString;
    }

    @Override
    public String getDisplay() {
        
        return display;
    }
}
