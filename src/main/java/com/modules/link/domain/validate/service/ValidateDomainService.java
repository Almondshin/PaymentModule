package com.modules.link.domain.validate.service;

import com.dsmdb.japi.MagicDBAPI;
import com.modules.link.enums.EnumAgency;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

@Component
public class ValidateDomainService {

    public String originalMessage(String encryptedData, String key, String iv) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(decodeKey(key), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(decodeIv(iv));
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public String encryptData(String targetEncode, String key, String iv) {
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

    public boolean verifyHmacSHA256(String originalMessage, String verifyInfo, String keyString) {
        try {
            String calculatedHmac = hmacSHA256(originalMessage, keyString);
            return verifyInfo.equals(calculatedHmac);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private static byte[] decodeKey(String key) {
        return Base64.getDecoder().decode(MagicDBAPI.decrypt("mokDBEnc", key));
    }

    private static byte[] decodeIv(String iv) {
        return Base64.getDecoder().decode(MagicDBAPI.decrypt("mokDBEnc", iv));
    }

    public boolean verifyMessageType(String receivedMessageType, String keyString) {
        // 메시지 타입 검증 로직
        switch (receivedMessageType) {
            case "SiteStatus": {
                if (!receivedMessageType.equals(EnumAgency.getMsgType(keyString, "status"))) {
                    return false;
                }
                break;
            }
            case "RegAgencySiteInfo": {
                if (!receivedMessageType.equals(EnumAgency.getMsgType(keyString, "reg"))) {
                    return false;
                }
                break;
            }
            case "CancelSiteInfo": {
                if (!receivedMessageType.equals(EnumAgency.getMsgType(keyString, "cancel"))) {
                    return false;
                }
                break;
            }
            case "NotifyPaymentSiteInfo": {
                if (!receivedMessageType.equals(EnumAgency.getMsgType(keyString, "paymentInfo"))) {
                    return false;
                }
                break;
            }
            case "NotifyStatusSite": {
                if (!receivedMessageType.equals(EnumAgency.getMsgType(keyString, "notification"))) {
                    return false;
                }
                break;
            }
            default:
                throw new IllegalStateException("존재하지 않는 messageType 입니다. 버전을 확인해주세요. " + receivedMessageType);
        }
        return true;
    }

    public String hmacSHA256(String target, String keyString) {
        try {
            byte[] hmacKey = keyString.getBytes(StandardCharsets.UTF_8);
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

    public boolean isSiteIdStartWithInitial(String agencyId, String siteId){
        for (EnumAgency enumAgency : EnumAgency.values()) {
            if (enumAgency.getCode().equals(agencyId) &&
                    siteId.toUpperCase().startsWith(enumAgency.getInitial())) {
                return true;
            }
        }
        return false;
    }

}
