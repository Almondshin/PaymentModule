package com.modules.link.domain.agency;

import com.modules.base.domain.ValueObject;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor
public class Company extends ValueObject<Company> {

    @Column(name = "SITE_NAME")
    private String siteName;
    @Column(name = "COMPANY_NAME")
    private String companyName;
    @Column(name = "BUSINESS_TYPE")
    private String businessType;
    @Column(name = "BIZ_NUMBER")
    private String bizNumber;
    @Column(name = "CEO_NAME")
    private String ceo;
    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;
    @Column(name = "ADDRESS")
    private String address;
    @Column(name = "COMPANY_SITE")
    private String companySite;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "SERVICE_USE_AGREE")
    private String serviceUseAgree;

    @Override
    public Object[] getEqualityFields() {
        return new Object[]{siteName, companyName, businessType, bizNumber, ceo, phoneNumber, address, companySite, email, serviceUseAgree};
    }

    @Builder
    public Company(String siteName, String companyName, String businessType, String bizNumber, String ceo, String phoneNumber, String address, String companySite, String email, String serviceUseAgree) {
        this.siteName = siteName;
        this.companyName = companyName;
        this.businessType = businessType;
        this.bizNumber = bizNumber;
        this.ceo = ceo;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.companySite = companySite;
        this.email = email;
        this.serviceUseAgree = serviceUseAgree;
    }
}
