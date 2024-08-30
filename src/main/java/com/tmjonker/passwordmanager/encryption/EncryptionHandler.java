package com.tmjonker.passwordmanager.encryption;

import com.google.crypto.tink.*;
import com.google.crypto.tink.aead.AesGcmKeyManager;
import com.google.crypto.tink.config.TinkConfig;
import com.tmjonker.passwordmanager.credentials.Credential;
import com.tmjonker.passwordmanager.users.User;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The class that handles all of the password encryption for PasswordManager.
 *
 * @author Tim Jonker
 */
public class EncryptionHandler {

    private final String KEYSET_FILE_PATH;
    private String keysetFileString;


    public EncryptionHandler() throws GeneralSecurityException {

        KEYSET_FILE_PATH = System.getProperty("user.dir") + "/";
        TinkConfig.register();
    }

    public byte[] encryptCredentials(String username, byte[] password, String identifier)
            throws GeneralSecurityException, IOException {

        KeysetHandle keysetHandle = KeysetHandle.generateNew(AesGcmKeyManager.aes128GcmTemplate());
        saveKeysetHandle(keysetHandle, identifier);
        Aead aead = keysetHandle.getPrimitive(Aead.class);

        return aead.encrypt(password, username.getBytes());
    }

    private void saveKeysetHandle(KeysetHandle keysetHandle, String identifier) throws IOException {

        StringBuilder keysetFileName = new StringBuilder(KEYSET_FILE_PATH);
        keysetFileName.append(identifier).append(".json");
        File keysetFile = new File(keysetFileName.toString());

        CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withFile(keysetFile));

        keysetFileString = keysetHandleToString(keysetFile);

        deleteKeysetHandle(identifier);
    }

    public String getKeysetFileString() {

        return keysetFileString;
    }

    private KeysetHandle loadKeysetHandle(Credential credential) throws GeneralSecurityException, IOException {

        File keysetFile = keysetHandleJsonCreator(credential.getKeysetHandleString(), credential.getIdentifier());
        KeysetHandle keysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(keysetFile));

        deleteKeysetHandle(credential.getIdentifier());

        return keysetHandle;
    }

    private KeysetHandle loadKeysetHandle(User user) throws GeneralSecurityException, IOException {

        File keysetFile = keysetHandleJsonCreator(user.getKeysetHandleString(), user.getIdentifier());
        KeysetHandle keysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(keysetFile));

        deleteKeysetHandle(user.getIdentifier());

        return keysetHandle;
    }

    public void deleteKeysetHandle(String identifier){

        StringBuilder keysetFileName = new StringBuilder(KEYSET_FILE_PATH);
        keysetFileName.append(identifier).append(".json");

        File keysetFile = new File(keysetFileName.toString());
        if (keysetFile.exists())
            keysetFile.delete();
    }

    public boolean verifyPassword(User user, byte[] password) throws GeneralSecurityException,
            IOException {

        KeysetHandle keysetHandle = loadKeysetHandle(user);

        Aead aead = keysetHandle.getPrimitive(Aead.class);
        byte[] decrypted = aead.decrypt(user.getPassword(), user.getUsername().getBytes());

        return Arrays.equals(decrypted, password);
    }

    public byte[] decryptCredentialPassword(Credential credential) throws GeneralSecurityException, IOException {

        KeysetHandle keysetHandle = loadKeysetHandle(credential);
        Aead aead = keysetHandle.getPrimitive(Aead.class);

        return aead.decrypt(credential.getPassword(), credential.getUsername().getBytes());
    }

    private String keysetHandleToString(File file) throws IOException {

        String a;
        StringBuilder b = new StringBuilder();

        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        while ((a = bufferedReader.readLine()) != null)
            b.append(a).append(System.lineSeparator());

        return b.toString();
    }

    private File keysetHandleJsonCreator(String keysetString, String identifier) throws IOException {

        StringBuilder keysetFileName = new StringBuilder(KEYSET_FILE_PATH);
        keysetFileName.append(identifier).append(".json");
        File keysetFile = new File(keysetFileName.toString());

        FileWriter fileWriter = new FileWriter(keysetFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write(keysetString);
        bufferedWriter.flush();

        return keysetFile;
    }

    private String convertUtf8(byte[] input) {

        return new String(input, StandardCharsets.UTF_8);
    }
}
