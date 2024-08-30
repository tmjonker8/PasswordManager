package com.tmjonker.passwordmanager.users;

import com.tmjonker.passwordmanager.credentials.Credential;
import com.tmjonker.passwordmanager.credentials.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {

    private byte[] password; //encrypted password
    private final String username;
    private Map<Type, ArrayList<Credential>> credentialCollection = new HashMap<>();

    private String identifier; // unique identifier. Used to link user to password list.

    private String keysetHandleString;

    public User(String username, String password) {
        if (password != null)
            this.password = password.getBytes();
        this.username = username;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {

        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getIdentifier() {

        return identifier;
    }

    public String getKeysetHandleString() {

        return keysetHandleString;
    }

    public void setIdentifier(int num) {

        identifier = "u" + num;
    }

    public Map<Type, ArrayList<Credential>> getCredentialCollection() {

        return credentialCollection;
    }

    public void setCredentialCollection(Map<Type, ArrayList<Credential>> collection) {

        credentialCollection = collection;
    }

    public void setKeysetHandleString(String keysetHandleString) {

        this.keysetHandleString = keysetHandleString;
    }
}
