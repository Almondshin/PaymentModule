package com.modules.adapter.in.models;

import com.modules.adapter.out.payment.config.hectofinancial.Constant;
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
    private static final String NUMBERS_ONLY_PATTERN = "^[0-9]+$";
    private static final int DAYS_BEFORE_EXPIRATION = 15;


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

    public ClientDataContainer(ClientDataContainer clientDataContainer, ClientDataContainer decryptInfo) {
        validateAgencyIdAndSiteId(clientDataContainer.agencyId, decryptInfo.siteId);
        this.agencyId = clientDataContainer.agencyId;
        this.siteId = decryptInfo.siteId;
    }

    public ClientDataContainer(ClientDataContainer clientDataContainer) {
        validateAgencyIdAndSiteId(agencyId, siteId);
        this.siteId = clientDataContainer.siteId;
        this.agencyId = clientDataContainer.agencyId;
        this.rateSel = clientDataContainer.rateSel;
        this.startDate = clientDataContainer.startDate;
    }

    public String keyString() {
        validateAgencyIdAndSiteId(agencyId, siteId);
        return this.agencyId;
    }
//    public Map<String, String> makeAgencyMapData(ClientDataModel clientDataModel) {
//        validateAgencyIdAndSiteId(clientDataModel.agencyId, clientDataModel.siteId);
//        Map<String, String> agencyMap = new HashMap<>();
//        agencyMap.put("agencyId", clientDataModel.agencyId);
//        agencyMap.put("siteId", clientDataModel.siteId);
//        return agencyMap;
//    }


    public boolean verifyHmacSHA256(String calculatedHmac, ClientDataContainer clientDataContainer) {
        String receivedHmac = clientDataContainer.verifyInfo;
        try {
            return receivedHmac.equals(calculatedHmac);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, String> makeVerifyMapData(ClientDataContainer clientDataContainer) {
        validateAgencyIdAndSiteId(clientDataContainer.agencyId, clientDataContainer.siteId);
        Map<String, String> verifyMap = new HashMap<>();
        verifyMap.put("verifyInfo", clientDataContainer.verifyInfo);
        verifyMap.put(clientDataContainer.agencyId, clientDataContainer.msgType);
        return verifyMap;
    }

    public Map<String, String> makeEncryptMapData(ClientDataContainer clientDataContainer) {
        validateAgencyIdAndSiteId(clientDataContainer.agencyId, clientDataContainer.siteId);
        Map<String, String> encryptDataMap = new HashMap<>();
        encryptDataMap.put("siteId", clientDataContainer.siteId);
        encryptDataMap.put("siteStatus", clientDataContainer.siteStatus);
        return encryptDataMap;
    }

    public Map<String, String> makeRegisterMapData(ClientDataContainer clientDataContainer) {
        validateAgencyIdAndSiteId(clientDataContainer.agencyId, clientDataContainer.siteId);
        Map<String, String> encryptDataMap = new HashMap<>();
        encryptDataMap.put("agencyId", clientDataContainer.agencyId);
        encryptDataMap.put("siteId", clientDataContainer.siteId);
        encryptDataMap.put("siteName", clientDataContainer.siteName);
        return encryptDataMap;
    }

    public Map<String, String> makeCancelMapData(ClientDataContainer clientDataContainer) {
        return createAgencySiteIdMap(clientDataContainer.agencyId, clientDataContainer.siteId);
    }

    public Map<String, String> makeClientInfoMap(ClientDataContainer clientDataContainer) {
        Map<String, String> clientInfoMap = new HashMap<>();
        clientInfoMap.put("companyName", clientDataContainer.companyName);
        clientInfoMap.put("bizNumber", clientDataContainer.bizNumber);
        clientInfoMap.put("ceoName", clientDataContainer.ceoName);
        return clientInfoMap;
    }

    public Map<String, String> checkedExtensionStatus(ClientDataContainer clientDataContainer) {
        if (clientDataContainer.extensionStatus.equals(EnumExtensionStatus.EXTENDABLE.getCode())) {
            return createAgencySiteIdMap(clientDataContainer.agencyId, clientDataContainer.siteId);
        }
        return Collections.emptyMap();
    }

    public boolean isActiveExtendableAutoRateAgency() {
        return this.siteStatus.equals(EnumSiteStatus.ACTIVE.getCode())
                && this.extensionStatus.equals(EnumExtensionStatus.EXTENDABLE.getCode())
                && this.scheduledRateSel != null
                && this.scheduledRateSel.toLowerCase().contains("auto");
    }

    public String concatenateSiteId(ClientDataContainer clientDataContainer) {
        validateAgencyIdAndSiteId(clientDataContainer.agencyId, clientDataContainer.siteId);
        return clientDataContainer.agencyId + "-" + clientDataContainer.siteId;
    }

    public String getAgencyStatus(ClientDataContainer searchedClient) {
        return searchedClient.siteStatus;
    }

    public String getAgencyScheduledRateSel(ClientDataContainer searchedClient) {
        return searchedClient.scheduledRateSel;
    }
    public String getClientMethod(ClientDataContainer receivedClient) {
        String method = receivedClient.method;
        if (!method.equals("card") && !method.equals("vbank")){
            throw new IllegalArgumentException("Invalid method");
        }
        return receivedClient.method;
    }
    public String getClientSalesPrice(ClientDataContainer receivedClient) {
        if(!isValidSalesPrice(receivedClient.salesPrice)){
            throw new IllegalArgumentException("Invalid sales price");
        }
        return receivedClient.salesPrice;
    }

    public boolean isCheckedAgencyScheduledRateAutoPay(ClientDataContainer searchedClient) {
        return searchedClient.scheduledRateSel != null && searchedClient.scheduledRateSel.toLowerCase().contains("autopay");
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

    private Map<String, String> createAgencySiteIdMap(String agencyId, String siteId) {
        validateAgencyIdAndSiteId(agencyId, siteId);
        Map<String, String> idMap = new HashMap<>();
        idMap.put("agencyId", agencyId);
        idMap.put("siteId", siteId);
        return idMap;
    }

    public String getMerchantIdBasedOnMethod(ClientDataContainer clientDataContainer, Constant constant){
        String merchantId;
        if (clientDataContainer.method.equals("card") && clientDataContainer.rateSel.toLowerCase().contains("autopay")) {
            merchantId = constant.PG_MID_AUTO;
        } else if (clientDataContainer.method.equals("card")) {
            merchantId = constant.PG_MID_CARD;
        } else {
            merchantId = constant.PG_MID;
        }
        return merchantId;
    }

    public String determineRateSelection(ClientDataContainer searchedClient, ClientDataContainer receivedClient) {
        String searchedRateSel = searchedClient.rateSel;
        if (searchedRateSel != null && !searchedRateSel.isEmpty()) {
            return searchedRateSel;
        }
        String receivedRateSel = receivedClient.rateSel;
        if (receivedRateSel != null && !receivedRateSel.isEmpty()) {
            return receivedRateSel;
        }
        return null;
    }


    public String decideStartDate(ClientDataContainer searchedClient, ClientDataContainer receivedClient) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDateInfo = searchedClient.startDate;
        Date startDateClient = receivedClient.startDate;

        // 초기 결제인 경우
        if (searchedClient.extensionStatus.equals(EnumExtensionStatus.DEFAULT.getCode())) {
            if (startDateClient != null) {
                return sdf.format(startDateClient);
            } else if (startDateInfo != null) {
                return sdf.format(startDateInfo);
            } else {
                return null;
            }
            // 연장이 활성화된 경우
        } else if (searchedClient.extensionStatus.equals(EnumExtensionStatus.EXTENDABLE.getCode())) {
            Date endDate = searchedClient.endDate;

            // 만료일로부터 15일 전 계산
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.DAY_OF_MONTH, -DAYS_BEFORE_EXPIRATION);
            Date fifteenDaysBeforeExpiration = calendar.getTime();

            Calendar yesterDayCal = Calendar.getInstance();
            yesterDayCal.add(Calendar.DATE, -1);
            Date yesterday = yesterDayCal.getTime();

            if (startDateClient != null) {
                // 요청된 시작일이 제공된 경우, 그 시작일은 만료일로부터 15일 전 이후여야 함
                if (startDateClient.after(fifteenDaysBeforeExpiration) && startDateClient.after(yesterday)) {
                    return sdf.format(startDateClient);
                } else {
                    // 잘못된 시작일을 처리하는 방법을 여기서 처리 (예외 던지기 등)
                    throw new NoExtensionException(EnumResultCode.NoExtension, searchedClient.siteId);
                }
            } else {
                // 요청된 시작일이 제공되지 않은 경우, 시작일을 만료일 다음 날짜로 설정
                Calendar nextEndDate = Calendar.getInstance();
                nextEndDate.setTime(endDate);
                nextEndDate.add(Calendar.DATE, 1);
                return sdf.format(nextEndDate.getTime());
            }
        } else {
            throw new NoExtensionException(EnumResultCode.NoExtension, searchedClient.siteId);
        }
    }


}
