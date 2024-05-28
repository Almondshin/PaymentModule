package com.modules.link.domain.agency;

import com.modules.base.domain.AggregateRoot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Table(name = "AGENCY_INFO")
public class Agency extends AggregateRoot<Agency, SiteId> {

    private static final String STATUS_TYPE = "status";
    private static final String REGISTER_TYPE = "reg";
    private static final String CANCEL_TYPE = "cancel";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Type(type = "com.modules.link.domain.agency.SiteId$SiteIdJavaType")
    @Column(name = "SITE_ID")
    private SiteId id;

    @Column(name = "AGENCY_ID")
    private String agencyId;

    @Column(name = "SITE_NAME")
    private String siteName;
    @Column(name = "COMPANY_NAME")
    private String companyName;
    @Column(name = "BUSINESS_TYPE")
    private String businessType;

    @Column(name = "BIZ_NUMBER")
    private String bizNumber;
    @Column(name = "CEO_NAME")
    private String ceoName;
    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;
    @Column(name = "ADDRESS")
    private String address;
    @Column(name = "COMPANY_SITE")
    private String companySite;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "RATE_SEL")
    private String rateSel;
    @Column(name = "SCHEDULED_RATE_SEL")
    private String scheduledRateSel;
    @Column(name = "SITE_STATUS")
    private String siteStatus;

    @Column(name = "EXTENSION_STATUS")
    private String extensionStatus;
    @Column(name = "EXCESS_COUNT")
    private String excessCount;

    @Column(name = "START_DATE")
    private Date startDate;
    @Column(name = "END_DATE")
    private Date endDate;

    @Column(name = "SETTLE_MANAGER_NAME")
    private String settleManagerName;
    @Column(name = "SETTLE_MANAGER_PHONE_NUMBER")
    private String settleManagerPhoneNumber;
    @Column(name = "SETTLE_MANAGER_TEL_NUMBER")
    private String settleManagerTelNumber;
    @Column(name = "SETTLE_MANAGER_EMAIL")
    private String settleManagerEmail;

    @Column(name = "SERVICE_USE_AGREE")
    private String serviceUseAgree;

    public String makeVerifyAndEncryptData(String type) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new HashMap<>();
        try {
            switch (type) {
                case STATUS_TYPE: {
                    map.put("siteId", this.id.toString());
                    map.put("agencyId", this.agencyId);
                    return mapper.writeValueAsString(map);
                }
                case REGISTER_TYPE:{
                    return mapper.writeValueAsString(this);
                }
                case CANCEL_TYPE:{
                    map.put("agencyId", this.agencyId);
                    map.put("siteId", this.id.toString());
                    map.put("siteName", this.siteName);
                    return mapper.writeValueAsString(map);
                }
            }
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public static Agency of(SiteId id, String agencyId, String siteName, String companyName, String businessType, String bizNumber, String ceoName, String phoneNumber, String address, String companySite, String email, String rateSel, Date startDate,  String settleManagerName, String settleManagerPhoneNumber, String settleManagerTelNumber, String settleManagerEmail, String serviceUseAgree) {
        Agency agency = new Agency();
        agency.id = id;
        agency.agencyId = agencyId;
        agency.siteName = siteName;
        agency.companyName = companyName;
        agency.businessType = businessType;
        agency.bizNumber = bizNumber;
        agency.ceoName = ceoName;
        agency.phoneNumber = phoneNumber;
        agency.address = address;
        agency.companySite = companySite;
        agency.email = email;
        agency.rateSel = rateSel;
        agency.startDate = startDate;
        agency.settleManagerName = settleManagerName;
        agency.settleManagerPhoneNumber = settleManagerPhoneNumber;
        agency.settleManagerTelNumber = settleManagerTelNumber;
        agency.settleManagerEmail = settleManagerEmail;
        agency.serviceUseAgree = serviceUseAgree;
        return agency;
    }


}
