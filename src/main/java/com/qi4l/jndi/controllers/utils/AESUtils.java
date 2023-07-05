package com.qi4l.jndi.controllers.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESUtils {
    private static final String ALGORITHM      = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int    KEY_SIZE       = 16;

    public static String decrypt(String ciphertext, String key) throws Exception {
        byte[] combinedBytes  = Base64.getDecoder().decode(ciphertext);
        byte[] ivBytes        = new byte[KEY_SIZE];
        byte[] encryptedBytes = new byte[combinedBytes.length - KEY_SIZE];

        System.arraycopy(combinedBytes, 0, ivBytes, 0, KEY_SIZE);
        System.arraycopy(combinedBytes, KEY_SIZE, encryptedBytes, 0, encryptedBytes.length);

        byte[]          keyBytes      = getKeyBytes(key);
        SecretKeySpec   secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM);
        IvParameterSpec ivSpec        = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private static byte[] getKeyBytes(String key) {
        byte[] keyBytes      = new byte[KEY_SIZE];
        byte[] passwordBytes = key.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(passwordBytes, 0, keyBytes, 0, Math.min(passwordBytes.length, keyBytes.length));
        return keyBytes;
    }
}
