package com.tmjonker.passwordmanager.encryption;

import java.nio.charset.StandardCharsets;

public class StringEncoder {

    public static String convertUtf8(byte[] input) {

        return new String(input, StandardCharsets.UTF_8);
    }
}
