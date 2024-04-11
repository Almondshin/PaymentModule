package com.modules.payment.application.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Map;

public class Utils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String mapToJSONString(Map<String, String> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            System.out.println("mapToJSONString 실패");
            return null;
        }
    }

    public static <T> T jsonStringToObject(String jsonString, Class<T> valueType) {
        try {
            return objectMapper.readValue(jsonString, valueType);
        } catch (JsonProcessingException e) {
            System.out.println("jsonStringToObject 실패");
//            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, "Failed to convert JSON string to object", e);
            return null;
        }
    }

    public static String hmacSHA256(String agencyKey, String agencyIv, String hmacKeyString) {
        // KEY + Message => Hash 생성
        // Hash Data (VerifyInfo) => Server
        // Server : EncryptData -> Decrypt ((KEY,AES) + IV) -> DecryptData
        // -> DecryptData + KEY -> Hash 생성 (calculatedHmac)
        // compare VerifyInfo(Hash Data), CalculatedHmac(Hash Data)
        String target = new String(decryptData(agencyKey, agencyIv, hmacKeyString));

        try {
            byte[] hmacKey = hmacKeyString.getBytes(StandardCharsets.UTF_8);
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(hmacKey, "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hashed = sha256_HMAC.doFinal(target.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] decryptData(String agencyKey, String agencyIv, String encryptData) {
        byte[] key = Base64.getDecoder().decode(agencyKey);
        byte[] iv = Base64.getDecoder().decode(agencyIv);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(Base64.getDecoder().decode(encryptData));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

}
