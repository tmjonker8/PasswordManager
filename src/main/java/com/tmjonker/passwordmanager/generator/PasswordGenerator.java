package com.tmjonker.passwordmanager.generator;

import java.util.Random;

public class PasswordGenerator {

    // 0-9 are included twice to increase likelihood of a numbers being included in generated password.
    private static final String CHARACTERS =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String SPECIAL_CHARACTERS =
            "!@$&*-_";

    public static String generatePassword() {

        Random random = new Random();
        StringBuilder password = new StringBuilder();
        String randomCharacter;

        for (int i = 0; i < 20; i++) {

            int randomInt;
            //Generates a password that contains a random symbol every 5 characters.
            if (i % 5 == 0 && i != 0) {
                randomInt = random.nextInt(SPECIAL_CHARACTERS.length());
                randomCharacter = SPECIAL_CHARACTERS.substring(randomInt, randomInt + 1);
            } else {
                randomInt = random.nextInt(CHARACTERS.length());
                randomCharacter = CHARACTERS.substring(randomInt, randomInt + 1);
            }

            password.append(randomCharacter);
        }
        return password.toString();
    }
}
