package com.modules.link.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public static <T> String objectToJSONString(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            System.out.println("objectToJSONString 실패: " + e.getMessage());
            return null;
        }
    }

    public static <T> T jsonStringToObject(String jsonString, Class<T> valueType) {
        try {
            return objectMapper.readValue(jsonString, valueType);
        } catch (JsonProcessingException e) {
            throw  new IllegalArgumentException("jsonStringToObject 실패", e);
        }
    }

//    /**
//     * HmacSHA256를 사용해 문자열을 해싱합니다.
//     *
//     * @param target        해싱 대상 문자열
//     * @param hmacKeyString Hmac 키 문자열
//     * @return HmacSHA256을 사용해 해싱된 문자열을 반환합니다. 예외 발생 시 null을 반환합니다.
//     */
//    public static String hmacSHA256(String target, String hmacKeyString) {
//        // KEY + Message => Hash 생성
//        // Hash Data (VerifyInfo) => Server
//        // Server : EncryptData -> Decrypt ((KEY,AES) + IV) -> DecryptData
//        // -> DecryptData + KEY -> Hash 생성 (calculatedHmac)
//        // compare VerifyInfo(Hash Data), CalculatedHmac(Hash Data)
//
//        try {
//            byte[] hmacKey = hmacKeyString.getBytes(StandardCharsets.UTF_8);
//            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
//            SecretKeySpec secret_key = new SecretKeySpec(hmacKey, "HmacSHA256");
//            sha256_HMAC.init(secret_key);
//            byte[] hashed = sha256_HMAC.doFinal(target.getBytes(StandardCharsets.UTF_8));
//            return Base64.getEncoder().encodeToString(hashed);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

//    /**
//     * AES 암호화를 사용하여 데이터를 암호화합니다.
//     *
//     * @param targetEncode 암호화 대상 데이터
//     * @return 암호화된 데이터 문자열
//     */
//
//    public static String encryptData(String targetEncode, byte[] key, byte[] iv) {
//        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
//        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
//        try {
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
//            byte[] cipherBytes = cipher.doFinal(targetEncode.getBytes(StandardCharsets.UTF_8));
//            return Base64.getEncoder().encodeToString(cipherBytes);
//        } catch (GeneralSecurityException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//    /**
//     * AES 암호화를 사용하여 데이터를 복호화합니다.
//     *
//     * @param targetDecode 복호화할 대상 데이터
//     * @return 복호화된 데이터 바이트 배열
//     */
//    public static byte[] decryptData(String targetDecode, byte[] key, byte[] iv) {
//        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
//        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
//        try {
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
//            return cipher.doFinal(Base64.getDecoder().decode(targetDecode));
//        } catch (GeneralSecurityException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
