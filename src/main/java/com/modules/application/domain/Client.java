package com.modules.application.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Client {
    /* 이용기관 정보 */
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

    private String serviceUseAgree;

    public Client(String siteName, String companyName, String businessType, String bizNumber, String ceoName, String phoneNumber, String address, String companySite, String email, String rateSel, String scheduledRateSel, String siteStatus, String extensionStatus,Date startDate, Date endDate, String serviceUseAgree) {
        this.siteName = siteName;
        this.companyName = companyName;
        this.businessType = businessType;
        this.bizNumber = bizNumber;
        this.ceoName = ceoName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.companySite = companySite;
        this.email = email;
        this.rateSel = rateSel;
        this.scheduledRateSel = scheduledRateSel;
        this.siteStatus = siteStatus;
        this.extensionStatus = extensionStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.serviceUseAgree = serviceUseAgree;
    }

    public Client(String rateSel, Date startDate, Date endDate) {
        this.rateSel = rateSel;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Client(String rateSel, String siteStatus, String extensionStatus, Date startDate, Date endDate) {
        this.rateSel = rateSel;
        this.siteStatus = siteStatus;
        this.extensionStatus = extensionStatus;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
