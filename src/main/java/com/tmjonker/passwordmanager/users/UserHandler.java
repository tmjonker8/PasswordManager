package com.tmjonker.passwordmanager.users;

import com.tmjonker.passwordmanager.credentials.Credential;
import com.tmjonker.passwordmanager.credentials.CredentialHandler;
import com.tmjonker.passwordmanager.credentials.Type;
import com.tmjonker.passwordmanager.encryption.EncryptionHandler;
import com.tmjonker.passwordmanager.gui.dialog.ExceptionDialog;
import com.tmjonker.passwordmanager.properties.PropertiesHandler;
import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * The class used to manipulate User objects.
 *
 * @author Tim Jonker
 */
public class UserHandler {

    private Logger logger = LogManager.getLogger(UserHandler.class);

    private final String USER_DIRECTORY = System.getProperty("user.dir") + "/user";
    private final String USER_FILE_NAME = USER_DIRECTORY + "/user.pm";
    private final File USER_FILE = new File(USER_FILE_NAME);

    private Map<String, User> userHashMap = new HashMap<>();

    private EncryptionHandler encryptionHandler = new EncryptionHandler();

    /**
     * Creates instance of UserHandler. Creates user directory if it doesn't exist and also creates the user.pm file
     * if it doesn't exist.
     *
     * @throws IOException      if there is an issue loading USER_FILE.
     */
    public UserHandler() throws IOException, GeneralSecurityException {

        if (!USER_FILE.exists()) {
            FileOutputStream fileOutputStream = new FileOutputStream(USER_FILE);
            fileOutputStream.flush();
            fileOutputStream.close();
        } else
            loadUserFile();
    }

    public void storeUser(User user) throws IOException {

        // makes sure that no decrypted passwords are stored on the hard drive.
        try {
            CredentialHandler credentialHandler = new CredentialHandler();
            credentialHandler.clearDecryptedPasswords(user.getCredentialCollection());
        } catch (GeneralSecurityException ex) {
            new ExceptionDialog(ex);
        }
        // adds user to Map and then saves the map to the hard drive.
        userHashMap.put(user.getUsername(), user);
        saveUserFile(userHashMap);
    }

    private void saveUserFile(Map<String, User> userHashMap) throws IOException {

        //converts Map<String,User> to byte[].
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bo);
        os.writeObject(userHashMap);
        os.flush();
        os.close();

        //encodes Map byte[] to a readable String using Base64.
        String userFile = new String(Base64.getEncoder().encode(bo.toByteArray()));

        //Writes String to file on hard drive.
        FileOutputStream outputStream = new FileOutputStream(USER_FILE);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(userFile);
        objectOutputStream.close();
    }

    private void loadUserFile() throws IOException {

        FileInputStream inputStream = new FileInputStream(USER_FILE);

        try {
            if (USER_FILE.length() != 0) {
                //loads String from user file.
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                String userFile = (String) objectInputStream.readObject();
                objectInputStream.close();

                //decodes the String from Base64 into default encoding.
                byte[] utf = Base64.getDecoder().decode(userFile.getBytes());

                //casts the byte[] into a HashMap<String,User>
                ByteArrayInputStream bi = new ByteArrayInputStream(utf);
                ObjectInputStream oi = new ObjectInputStream(bi);
                userHashMap = (HashMap<String, User>) oi.readObject();
                oi.close();
            }
        } catch (EOFException | ClassNotFoundException ex) {
            logger.error(ex);
            logger.addAppender(new FileAppender());
        }
    }

    public User createUser(String username, byte[] password) throws GeneralSecurityException, IOException {

        PropertiesHandler.incrementAccountsNum(); //increments total # of accounts by 1.

        User newUser = new User(username,null);
        newUser.setIdentifier(PropertiesHandler.getAccountsNum()); // sets identifier as the current total # of accounts.
        newUser.setPassword(encryptionHandler.encryptCredentials(username, password,
                newUser.getIdentifier()));
        newUser.setKeysetHandleString(encryptionHandler.getKeysetFileString());
        newUser.setCredentialCollection(generateCollection());

        return newUser;
    }

    private Map<Type, ArrayList<Credential>> generateCollection() {

        Map<Type, ArrayList<Credential>> collection = new HashMap<>();

        for (Type type : Type.values())
            collection.put(type, new ArrayList<>());

        return collection;
    }

    public boolean checkUsernameExists(String username) {

        return userHashMap.containsKey(username);
    }

    public boolean validateReturningUser(User desiredUser, byte[] attemptedPassword) throws GeneralSecurityException,
            IOException {

        if (encryptionHandler.verifyPassword(desiredUser, attemptedPassword)) {
            return true;
        } else
            return false;
    }

    public User loadUser(String username) {

        return userHashMap.get(username);
    }

    public void updateEncryption(User user, byte[] unencryptedPassword) throws GeneralSecurityException, IOException {

        EncryptionHandler encryptionHandler = new EncryptionHandler();
        user.setPassword(encryptionHandler.encryptCredentials(user.getUsername(),
                unencryptedPassword,
                user.getIdentifier())); // generates a new keyset handle and re-encrypts password.
        user.setKeysetHandleString(encryptionHandler.getKeysetFileString());
        storeUser(user);
    }

    public void storeCredential(User user, Credential credential) throws IOException, GeneralSecurityException {

        Map<Type, ArrayList<Credential>> credentialMap = user.getCredentialCollection();
        ArrayList<Credential> credentialList = credentialMap.get(credential.getType());
        credentialList.add(credential);
        credentialMap.put(credential.getType(), credentialList);
        user.setCredentialCollection(credentialMap);
        storeUser(user);
    }

    public void removeCredential(User user, Credential credential) throws IOException {

        Map<Type, ArrayList<Credential>> credentialCollection = user.getCredentialCollection();
        ArrayList<Credential> credentialList = credentialCollection.get(credential.getType());

        Credential toBeRemoved = null;

        for (Credential c : credentialList) {
            if (c.equals(credential)) {
                toBeRemoved = c;
            }
        }

        if (toBeRemoved != null)
            credentialList.remove(toBeRemoved);

        credentialCollection.put(credential.getType(), credentialList);
        user.setCredentialCollection(credentialCollection);
        storeUser(user);
    }
}
