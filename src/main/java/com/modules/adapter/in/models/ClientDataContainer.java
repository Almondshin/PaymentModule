package com.modules.adapter.in.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modules.adapter.out.payment.config.hectofinancial.Constant;
import com.modules.application.enums.EnumAgency;
import com.modules.application.enums.EnumExtensionStatus;
import com.modules.application.enums.EnumSiteStatus;
import com.modules.application.exceptions.enums.EnumResultCode;
import com.modules.application.exceptions.exceptions.IllegalAgencyIdSiteIdException;
import com.modules.application.exceptions.exceptions.NoExtensionException;
import lombok.Setter;
import lombok.ToString;

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


    private boolean isValidAgencyId(String agencyId) {
        return agencyId.matches(AGENCY_SITE_ID_PATTERN);
    }

    private boolean isValidSiteId(String siteId) {
        return siteId.matches(AGENCY_SITE_ID_PATTERN);
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

    public boolean verifyHmacSHA256(String calculatedHmac) {
        String receivedHmac = this.verifyInfo;
        return receivedHmac.equals(calculatedHmac);
    }

    public boolean verifyReceivedMessageType(String messageType) {
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

}
