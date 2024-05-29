package com.modules.link.utils;

import com.dsmdb.japi.MagicDBAPI;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

public class SecurityUtils {

    public static String encryptData(String targetEncode, String key, String iv) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(decodeKey(key), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(decodeIv(iv));
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] cipherBytes = cipher.doFinal(targetEncode.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(cipherBytes);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decryptData(String targetDecode, String key, String iv) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(decodeKey(key), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(decodeIv(iv));
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(Base64.getDecoder().decode(targetDecode));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] decodeKey(String key) {
        return Base64.getDecoder().decode(MagicDBAPI.decrypt("mokDBEnc", key));
    }

    private static byte[] decodeIv(String iv) {
        return Base64.getDecoder().decode(MagicDBAPI.decrypt("mokDBEnc", iv));
    }
}
