package com.modules.payment.domain;

import com.modules.payment.application.Config.Constant;
import com.modules.payment.application.enums.EnumAgency;
import com.modules.payment.application.enums.EnumExtensionStatus;
import com.modules.payment.application.enums.EnumSiteStatus;
import com.modules.payment.application.exceptions.enums.EnumResultCode;
import com.modules.payment.application.exceptions.exceptions.IllegalAgencyIdSiteIdException;
import com.modules.payment.application.exceptions.exceptions.IllegalStatusException;
import com.modules.payment.application.exceptions.exceptions.NoExtensionException;
import com.modules.payment.application.utils.PGUtils;
import com.modules.payment.application.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Stream;

@ToString
@AllArgsConstructor
public class Agency {

    private static final String AGENCY_SITE_ID_PATTERN = "^[a-zA-Z0-9\\-]+$";
    private static final String SCHEDULED_RATE_SEL = "autopay";

    private static final String CANCEL = "cancel";
    private static final String EXTEND = "extend";

    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.now();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");

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

    private String msgType;
    private String encryptData;
    private String verifyInfo;

    private String settleManagerName;
    private String settleManagerPhoneNumber;
    private String settleManagerTelNumber;
    private String settleManagerEmail;

    private String serviceUseAgree;
    private String privateColAgree;
    private String thirdProvAgree;

    private String method;
    private String salesPrice;


    private boolean isValidAgencyId(String agencyId) {
        return agencyId.matches(AGENCY_SITE_ID_PATTERN);
    }

    private boolean isValidSiteId(String siteId) {
        return siteId.matches(AGENCY_SITE_ID_PATTERN);
    }

    public Agency(String type, String agencyId, String siteId) {
        this.agencyId = agencyId;
        this.siteId = siteId;

        if (type.equals(CANCEL)) {
            this.agencyId = agencyId;
            this.siteId = siteId.split("-")[1];
        }
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

    public String agencyId() {
        validateAgencyIdAndSiteId(agencyId, siteId);
        return this.agencyId;
    }

    public String agencyEncryptData() {
        return this.encryptData;
    }

    public boolean isVerifiedHmac(String agencyKey, String agencyIv) {
        return this.verifyInfo.equals(Utils.hmacSHA256(agencyKey, agencyIv, this.agencyId));
    }

    private boolean isValidRateSel() {
        return this.rateSel != null;
    }

    public String rateSel(Agency searchedAgency) {
        return Stream.of(this, searchedAgency)
                .filter(Agency::isValidRateSel)
                .map(agency -> agency.rateSel)
                .findFirst()
                .orElse("Error: Cannot get rateSel as it was not found");
    }

    public Agency registerAgencyInfo(String agencyKey, String agencyIv) {
        String registerInfo = new String(Utils.decryptData(agencyKey, agencyIv, this.encryptData));
        return Utils.jsonStringToObject(registerInfo, Agency.class);
    }

    public String startDate(Agency searchedAgency) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        EnumExtensionStatus extensionStatus = Arrays.stream(EnumExtensionStatus.values())
                .filter(e -> this.extensionStatus.equals(e.getCode()))
                .findFirst()
                .orElse(null);

        switch (Objects.requireNonNull(extensionStatus)) {
            case DEFAULT: {
                if (this.startDate != null) {
                    return sdf.format(this.startDate);
                }
                if (searchedAgency.startDate != null) {
                    return sdf.format(searchedAgency.startDate);
                }
            }
            case EXTENDABLE: {
                Date endDate = searchedAgency.endDate;
                Calendar nextEndDate = Calendar.getInstance();
                nextEndDate.setTime(endDate);
                nextEndDate.add(Calendar.DATE, 1);
                return sdf.format(nextEndDate.getTime());
            }
            default: {
                throw new NoExtensionException(EnumResultCode.NoExtension, searchedAgency.siteId);
            }
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

    public Map<String, String> generateMapData(String mapType) {
        validateAgencyIdAndSiteId(this.agencyId, this.siteId);
        Map<String, String> dataMap = new HashMap<>();

        switch (mapType) {
            case "encrypt":
                dataMap.put("siteId", this.siteId);
                dataMap.put("siteStatus", this.siteStatus);
                break;
            case "AdminNotification":
                dataMap.put("agencyId", this.agencyId);
                dataMap.put("siteId", this.siteId);
                dataMap.put("siteName", this.siteName);
                break;
        }

        return dataMap;
    }

    public void validateRequiredValues() {
        Optional<String> missingField = this.checkRequiredFields();
        if (missingField.isPresent()) {
            throw new IllegalArgumentException(missingField + " 필드가 비어 있습니다.");
        }
    }

    private Optional<String> checkRequiredFields() {
        return Stream.<Entry<String, Supplier<String>>>of(
                        new SimpleEntry<>("agencyId", () -> this.agencyId),
                        new SimpleEntry<>("siteId", () -> this.siteId),
                        new SimpleEntry<>("siteName", () -> this.siteName),
                        new SimpleEntry<>("companyName", () -> this.companyName),
                        new SimpleEntry<>("bizNumber", () -> this.bizNumber),
                        new SimpleEntry<>("ceoName", () -> this.ceoName),
                        new SimpleEntry<>("phoneNumber", () -> this.phoneNumber),
                        new SimpleEntry<>("address", () -> this.address),
                        new SimpleEntry<>("companySite", () -> this.companySite),
                        new SimpleEntry<>("settleManagerName", () -> this.settleManagerName),
                        new SimpleEntry<>("settleManagerPhoneNumber", () -> this.settleManagerPhoneNumber),
                        new SimpleEntry<>("settleManagerTelNumber", () -> this.settleManagerTelNumber),
                        new SimpleEntry<>("settleManagerEmail", () -> this.settleManagerEmail)
                )
                .map(e -> new SimpleEntry<>(e.getKey(), e.getValue().get()))
                .filter(e -> e.getValue() == null || e.getValue().isEmpty())
                .map(Entry::getKey)
                .findFirst();
    }

    private EnumSiteStatus checkedSiteStatus() {
        return Arrays.stream(EnumSiteStatus.values())
                .filter(e -> this.siteStatus.equals(e.getCode()))
                .findFirst()
                .orElse(null);
    }

    public void isActive() {
        EnumSiteStatus status = this.checkedSiteStatus();
        switch (status) {
            case SUSPENDED:
                throw new IllegalStatusException(EnumResultCode.SuspendedSiteId);
            case REJECT:
                throw new IllegalStatusException(EnumResultCode.RejectAgency);
            case PENDING:
                throw new IllegalStatusException(EnumResultCode.PendingApprovalStatus);
        }
    }

    public void isScheduledRateSel() {
        if (this.scheduledRateSel.toLowerCase().contains(SCHEDULED_RATE_SEL)) {
            throw new IllegalStatusException(EnumResultCode.Subscription);
        }
    }

    public boolean isExtendable() {
        EnumExtensionStatus status = Arrays.stream(EnumExtensionStatus.values())
                .filter(e -> this.extensionStatus.equals(e.getCode()))
                .findFirst()
                .orElse(null);
        return status != null && status.getCode().equals(EnumExtensionStatus.EXTENDABLE.getCode());
    }

    public Agency checkedExtendable() {
        EnumExtensionStatus status = Arrays.stream(EnumExtensionStatus.values())
                .filter(e -> Optional.ofNullable(this.extensionStatus)
                        .orElseThrow(() -> new NullPointerException("extensionStatus is null"))
                        .equals(e.getCode()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No matching EnumExtensionStatus found for code: " + this.extensionStatus));

        if (status.getCode().equals(EnumExtensionStatus.EXTENDABLE.getCode())) {
            return new Agency(EXTEND, this.agencyId, this.siteId);
        }

        throw new NoExtensionException(EnumResultCode.NoExtension, siteId);
    }

    public List<String> makeCompanyInfo() {
        return List.of(this.companyName, this.bizNumber, this.ceoName);
    }

    private String selectMerchantId() {
        String merchantId;
        Constant constant = new Constant();
        if (this.method.equals("card") && this.rateSel.toLowerCase().contains("autopay")) {
            merchantId = constant.PG_MID_AUTO;
        } else if (this.method.equals("card")) {
            merchantId = constant.PG_MID_CARD;
        } else {
            merchantId = constant.PG_MID;
        }
        return merchantId;
    }


    public PGResponseManager pgResponseMsg(String tradeNum) {
        String trdDt = LOCAL_DATE_TIME.format(DATE_FORMATTER);
        String trdTm = LOCAL_DATE_TIME.format(TIME_FORMATTER);
        String hashCipher = hashCipher(tradeNum, trdDt, trdTm);
        return new PGResponseManager(selectMerchantId(), this.method, tradeNum, this.salesPrice, trdDt, trdTm, hashCipher, encParams());
    }

    private String hashCipher(String tradeNum, String trdDt, String trdTm) {
        Constant constant = new Constant();
        String merchantId = selectMerchantId();
        String paymentType = this.method;
        String hashPlain = String.join("", merchantId, paymentType, tradeNum, trdDt, trdTm, this.salesPrice, constant.LICENSE_KEY);
        try {
            return PGUtils.digestSHA256(hashPlain);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HashMap<String, String> encParams() {
        Constant constant = new Constant();
        HashMap<String, String> params = new HashMap<>();
        if (this.salesPrice != null && !this.salesPrice.isEmpty()) {
            try {
                byte[] aesCipherRaw = PGUtils.aes256EncryptEcb(constant.AES256_KEY, this.salesPrice);
                String aesCipher = PGUtils.encodeBase64(aesCipherRaw);
                params.put("trdAmt", aesCipher);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return params;
    }

    public boolean isCurrentScheduledRateSel() {
        return this.siteStatus.equals(EnumSiteStatus.ACTIVE.getCode())
                && this.extensionStatus.equals(EnumExtensionStatus.EXTENDABLE.getCode())
                && this.scheduledRateSel != null
                && this.scheduledRateSel.toLowerCase().contains("auto");
    }

    public String selectRateSelBasedOnType(String type) {
        if (type.equals("scheduled")) {
            return this.scheduledRateSel;
        }
        return this.rateSel;
    }
}
