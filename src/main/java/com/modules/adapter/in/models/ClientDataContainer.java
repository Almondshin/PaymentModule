package com.modules.adapter.in.models;

import com.modules.adapter.out.payment.config.hectofinancial.Constant;
import com.modules.application.enums.EnumAgency;
import com.modules.application.enums.EnumExtensionStatus;
import com.modules.application.enums.EnumSiteStatus;
import com.modules.application.exceptions.enums.EnumResultCode;
import com.modules.application.exceptions.exceptions.IllegalAgencyIdSiteIdException;
import com.modules.application.exceptions.exceptions.NoExtensionException;
import com.modules.application.utils.Utils;
import lombok.Setter;
import lombok.ToString;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.*;

@Setter
@ToString
public class ClientDataContainer {
    private String agencyId;
    private String siteId;

    private String siteName;
    private String companyName;
    private String businessType;
    private String bizNumber;
    private String ceoName;
    private String phoneNumber;
    private String address;
    private String companySite;
    private String email;
    private String rateSel;
    private String scheduledRateSel;
    private String siteStatus;
    private String extensionStatus;
    private Date startDate;

    private Date endDate;
    private String salesPrice;
    private String offer;
    private String method;

    private String settleManagerName;
    private String settleManagerPhoneNumber;
    private String settleManagerTelNumber;
    private String settleManagerEmail;

    private String serviceUseAgree;
    private String privateColAgree;
    private String thirdProvAgree;

    private String msgType;
    private String encryptData;
    private String verifyInfo;

    private String refundAcntNo;
    private String vAcntNo;
    private String cnclAmt;
    private String trdAmt;
    private String vatAmt;
    private String taxFreeAmt;


    private static final String AGENCY_SITE_ID_PATTERN = "^[a-zA-Z0-9\\-]+$";

    private static final String NUMBERS_ONLY_PATTERN = "^[0-9]+$";

    private boolean isValidAgencyId(String agencyId) {
        return agencyId.matches(AGENCY_SITE_ID_PATTERN);
    }

    private boolean isValidSiteId(String siteId) {
        return siteId.matches(AGENCY_SITE_ID_PATTERN);
    }

    private boolean isValidSalesPrice(String salesPrice) {
        return salesPrice.matches(NUMBERS_ONLY_PATTERN);
    }

    private void validateAgencyIdAndSiteId(String agencyId, String siteId) {
        if (!isValidAgencyId(agencyId) || !isValidSiteId(siteId)) {
            throw new IllegalAgencyIdSiteIdException(EnumResultCode.IllegalArgument, siteId);
        }
    }

    public String keyString() {
        validateAgencyIdAndSiteId(agencyId, siteId);
        return this.agencyId;
    }

    public String clientEncryptData() {
        return this.encryptData;
    }

    public boolean isVerifiedHmac(String agencyKey, String agencyIv) {
        return this.verifyInfo.equals(hmacSHA256(agencyKey, agencyIv));
    }

    public ClientDataContainer registerAgencyInfo(String agencyKey, String agencyIv) {
        String registerInfo = new String(decryptData(agencyKey, agencyIv));
        return Utils.jsonStringToObject(registerInfo, ClientDataContainer.class);
    }

    private String hmacSHA256(String agencyKey, String agencyIv) {
        // KEY + Message => Hash 생성
        // Hash Data (VerifyInfo) => Server
        // Server : EncryptData -> Decrypt ((KEY,AES) + IV) -> DecryptData
        // -> DecryptData + KEY -> Hash 생성 (calculatedHmac)
        // compare VerifyInfo(Hash Data), CalculatedHmac(Hash Data)
        String hmacKeyString = this.agencyId;
        String target = new String(decryptData(agencyKey, agencyIv));

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

    private byte[] decryptData(String agencyKey, String agencyIv) {
        byte[] key = Base64.getDecoder().decode(agencyKey);
        byte[] iv = Base64.getDecoder().decode(agencyIv);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(Base64.getDecoder().decode(this.encryptData));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isVerifiedMessageType(String messageType) {
        boolean isCancelType = messageType.equals("cancel");
        boolean isRegType = messageType.equals("reg");
        boolean isGetType = messageType.equals("status");

        EnumAgency[] enumAgencies = EnumAgency.values();
        for (EnumAgency enumAgency : enumAgencies) {
            if (enumAgency.getCode().equals(keyString())) {
                if (isCancelType) {
                    return enumAgency.getCancelMsg().equals(messageType);
                } else if (isRegType) {
                    return enumAgency.getRegMsg().equals(messageType);
                } else if (isGetType) {
                    return enumAgency.getStatusMsg().equals(messageType);
                }
            }
        }
        return false;
    }

    public Map<String, String> makeEncryptMapData() {
        validateAgencyIdAndSiteId(this.agencyId, this.siteId);
        Map<String, String> encryptDataMap = new HashMap<>();
        encryptDataMap.put("siteId", this.siteId);
        encryptDataMap.put("siteStatus", this.siteStatus);
        return encryptDataMap;
    }

    public Map<String, String> notificationData() {
        validateAgencyIdAndSiteId(this.agencyId, this.siteId);
        Map<String, String> notificationDataMap = new HashMap<>();
        notificationDataMap.put("agencyId", this.agencyId);
        notificationDataMap.put("siteId", this.siteId);
        notificationDataMap.put("siteName", this.siteName);
        return notificationDataMap;
    }

    public String checkRequiredFields(ClientDataContainer clientDataContainer) {
        Map<String, String> requiredFields = new LinkedHashMap<>();
        requiredFields.put("siteName", clientDataContainer.siteName);
        requiredFields.put("companyName", clientDataContainer.companyName);
        requiredFields.put("bizNumber", clientDataContainer.bizNumber);
        requiredFields.put("ceoName", clientDataContainer.ceoName);
        requiredFields.put("phoneNumber", clientDataContainer.phoneNumber);
        requiredFields.put("address", clientDataContainer.address);
        requiredFields.put("companySite", clientDataContainer.companySite);
        requiredFields.put("settleManagerName", clientDataContainer.settleManagerName);
        requiredFields.put("settleManagerPhoneNumber", clientDataContainer.settleManagerPhoneNumber);
        requiredFields.put("settleManagerTelNumber", clientDataContainer.settleManagerTelNumber);
        requiredFields.put("settleManagerEmail", clientDataContainer.settleManagerEmail);

        for (Map.Entry<String, String> field : requiredFields.entrySet()) {
            if (field.getValue() == null || field.getValue().isEmpty()) {
                return field.getKey();
            }
        }
        return null;
    }


    /* 표준 결제창 정보 세팅 관련 */
    public String agencyIdForRetrieve() {
        validateAgencyIdAndSiteId(agencyId, siteId);
        return this.agencyId;
    }

    public String decideRateSel(ClientDataContainer searchedClient) {
        String searchedRateSel = searchedClient.rateSel;
        if (searchedRateSel != null && !searchedRateSel.isEmpty()) {
            return searchedRateSel;
        }
        String receivedRateSel = this.rateSel;
        if (receivedRateSel != null && !receivedRateSel.isEmpty()) {
            return receivedRateSel;
        }
        return null;
    }

    public String decideStartDate(ClientDataContainer searchedClient) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        EnumExtensionStatus extensionStatus = Arrays.stream(EnumExtensionStatus.values())
                .filter(e -> this.extensionStatus.equals(e.getCode()))
                .findFirst()
                .orElse(null);

        switch (Objects.requireNonNull(extensionStatus)) {
            case DEFAULT: {
                if (this.startDate != null) {
                    return sdf.format(this.startDate);
                } else if (searchedClient.startDate != null) {
                    return sdf.format(searchedClient.startDate);
                } else {
                    return null;
                }
            }
            case EXTENDABLE: {
                Date endDate = searchedClient.endDate;
                Calendar nextEndDate = Calendar.getInstance();
                nextEndDate.setTime(endDate);
                nextEndDate.add(Calendar.DATE, 1);
                return sdf.format(nextEndDate.getTime());
            }
            default: {
                throw new NoExtensionException(EnumResultCode.NoExtension, searchedClient.siteId);
            }
        }
    }

    public EnumSiteStatus checkedSiteStatus() {
        return Arrays.stream(EnumSiteStatus.values())
                .filter(e -> this.siteStatus.equals(e.getCode()))
                .findFirst()
                .orElse(null);
    }


    public Map<String, String> checkedExtendable() {
        EnumExtensionStatus status = Arrays.stream(EnumExtensionStatus.values())
                .filter(e -> this.extensionStatus.equals(e.getCode()))
                .findFirst()
                .orElse(null);

        if (status != null && status.getCode().equals(EnumExtensionStatus.EXTENDABLE.getCode())) {
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("agencyId", this.agencyId);
            resultMap.put("siteId", this.siteId);
            return resultMap;
        }

        return null;
    }

    public Map<String, String> makeCompanyInfo() {
        Map<String, String> companyMap = new HashMap<>();
        companyMap.put("companyName", this.companyName);
        companyMap.put("bizNumber", this.bizNumber);
        companyMap.put("ceoName", this.ceoName);
        return companyMap;
    }


    /* PG사로 결제 정보 세팅 관련 */
    public String generateMerchantId(Constant constant) {
        String merchantId;
        if (this.method.equals("card") && this.rateSel.toLowerCase().contains("autopay")) {
            merchantId = constant.PG_MID_AUTO;
        } else if (this.method.equals("card")) {
            merchantId = constant.PG_MID_CARD;
        } else {
            merchantId = constant.PG_MID;
        }
        return merchantId;
    }

    public String fetchPaymentMethod() {
        return this.method;
    }

    public String fetchPaymentPrice() {
        if (!isValidSalesPrice(this.salesPrice)) {
            throw new IllegalArgumentException("Invalid sales price");
        }
        return this.salesPrice;
    }

    /*정기 결제 관련*/
    public boolean isActiveAndExtendableSiteAndScheduledRateSel() {
        return this.siteStatus.equals(EnumSiteStatus.ACTIVE.getCode())
                && this.extensionStatus.equals(EnumExtensionStatus.EXTENDABLE.getCode())
                && this.scheduledRateSel != null
                && this.scheduledRateSel.toLowerCase().contains("auto");
    }

    public boolean isScheduledPaymentEnabled() {
        return (this.scheduledRateSel != null && !this.scheduledRateSel.isEmpty());
    }

    public String rateSelForRetrieve(String type) {
        if (type.equals("scheduled")) {
            return this.scheduledRateSel;
        }
        return this.rateSel;
    }

    public boolean isCheckedAgencyScheduledRateAutoPay() {
        return this.scheduledRateSel != null && this.scheduledRateSel.toLowerCase().contains("autopay");
    }
}
