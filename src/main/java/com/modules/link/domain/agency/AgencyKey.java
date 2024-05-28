package com.modules.link.domain.agency;

import com.modules.base.domain.AggregateRoot;
import com.modules.link.enums.EnumAgency;
import com.modules.link.enums.EnumResultCode;
import com.modules.link.utils.Utils;
import com.dsmdb.japi.MagicDBAPI;
import lombok.Getter;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.*;


@Entity
@Table(name = "AGENCY_INFO_KEY")
public class AgencyKey extends AggregateRoot<AgencyKey, String> {

    @Id
    @Column(name = "AGENCY_ID")
    @Getter
    private String id;

    @Column(name = "AGENCY_KEY")
    private String agencyKey;
    @Column(name = "AGENCY_IV")
    private String agencyIv;

    @Column(name = "AGENCY_URL")
    private String agencyUrl;
    @Column(name = "AGENCY_PRODUCT_TYPE")
    private String productList;
    @Column(name = "BILLING_BASE")
    private String billingBase;

    public String originalMessage(String encryptData) {
        return new String(decryptData(encryptData));
    }

    public Optional<EnumResultCode> validateHmacAndMsgType(String receivedMessageType, String encryptedData, String verifyInfo) {
        if (!verifyHmacSHA256(originalMessage(encryptedData), verifyInfo)) {
            return Optional.of(EnumResultCode.HmacError);
        }
        if (!verifyMessageType(receivedMessageType)){
            return Optional.of(EnumResultCode.MsgTypeError);
        }
        return Optional.empty();
    }

    public String encryptData(String targetEncode) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(decodeKey(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(decodeIv());
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] cipherBytes = cipher.doFinal(targetEncode.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(cipherBytes);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] decryptData(String targetDecode) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(decodeKey(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(decodeIv());
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(Base64.getDecoder().decode(targetDecode));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public String hmacSHA256(String target) {
        try {
            byte[] hmacKey = keyString().getBytes(StandardCharsets.UTF_8);
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

    public String getAgencyURL(String type) {
        Map<String, String> agencyMap = Utils.jsonStringToObject(this.agencyUrl, Map.class);
        if (agencyMap == null || agencyMap.isEmpty()) {
            throw new IllegalStateException("Agency URL is null or empty");
        }
        switch (type) {
            case "PAYMENT":
                return agencyMap.get("NotifyPaymentSiteInfo");
            case "STATUS":
                return agencyMap.get("NotifyStatusSite");
            default:
                throw new IllegalStateException("'" + type + "'는 존재하지 않는 타입 입니다.");
        }
    }

    public List<String> getProductList(String agencyId) {
        List<String> productList = new ArrayList<>();
        for (String product : this.productList.split(",")) {
            if (product.startsWith(agencyId)) {
                productList.add(product);
            }
        }
        return productList;
    }


    private String keyString() {
        return this.id;
    }

    private byte[] decodeKey() {
        return Base64.getDecoder().decode(MagicDBAPI.decrypt("mokDBEnc", this.agencyKey));
    }

    private byte[] decodeIv() {
        return Base64.getDecoder().decode(MagicDBAPI.decrypt("mokDBEnc", this.agencyIv));
    }

    private boolean verifyHmacSHA256(String originalMessage, String verifyInfo) {
        try {
            String calculatedHmac = hmacSHA256(originalMessage);
            return verifyInfo.equals(calculatedHmac);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean verifyMessageType(String receivedMessageType) {
        switch (receivedMessageType) {
            case "SiteStatus": {
                if(!receivedMessageType.equals(EnumAgency.getMsgType(keyString(), "status"))){
                    return false;
                }
                break;
            }
            case "RegAgencySiteInfo": {
                if(!receivedMessageType.equals(EnumAgency.getMsgType(keyString(), "regInfo"))){
                    return false;
                }
                break;
            }
            case "CancelSiteInfo":{
                if(!receivedMessageType.equals(EnumAgency.getMsgType(keyString(), "cancel"))){
                    return false;
                }
                break;
            }
            case "NotifyPaymentSiteInfo":{
                if(!receivedMessageType.equals(EnumAgency.getMsgType(keyString(), "paymentInfo"))){
                    return false;
                }
                break;
            }
            case "NotifyStatusSite":{
                if(!receivedMessageType.equals(EnumAgency.getMsgType(keyString(), "notification"))){
                    return false;
                }
                break;
            }
            default:
                throw new IllegalStateException("존재하지 않는 messageType 입니다. 버전을 확인해주세요. " + receivedMessageType);
        }
        return true;
    }
}