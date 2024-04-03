package com.modules.adapter.in.models;

import com.modules.application.exceptions.enums.EnumResultCode;
import com.modules.application.exceptions.exceptions.IllegalAgencyIdSiteIdException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class ClientDataModel {
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

    public ClientDataModel() {
    }

    public ClientDataModel(String agencyId, String siteId) {
        if (!isValidAgencyId(agencyId) || !isValidSiteId(siteId)) {
            throw new IllegalAgencyIdSiteIdException(EnumResultCode.IllegalArgument, siteId);
        }
        this.siteId = siteId;
        this.agencyId = agencyId;
    }

    public ClientDataModel(String agencyId, String siteId, String rateSel, Date startDate) {
        if (!isValidAgencyId(agencyId) || !isValidSiteId(siteId)) {
            throw new IllegalAgencyIdSiteIdException(EnumResultCode.IllegalArgument, siteId);
        }
        this.siteId = siteId;
        this.agencyId = agencyId;
        this.rateSel = rateSel;
        this.startDate = startDate;
    }

    private boolean isValidAgencyId(String agencyId) {
        return agencyId.matches(AGENCY_SITE_ID_PATTERN);
    }

    private boolean isValidSiteId(String siteId) {
        return  siteId.matches(AGENCY_SITE_ID_PATTERN);
    }

}
