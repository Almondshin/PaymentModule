package com.modules.link.domain.agency;

import com.modules.base.domain.ValueObject;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Embeddable
@NoArgsConstructor
public class AgencyCompany extends ValueObject<AgencyCompany> {

    @Column(name = "SITE_NAME")
    @NotBlank(message = "siteName")
    private String siteName;

    @Column(name = "COMPANY_NAME")
    @NotBlank(message = "companyName")
    private String companyName;

    @Column(name = "BUSINESS_TYPE")
    @NotBlank(message = "businessType")
    private String businessType;

    @Column(name = "BIZ_NUMBER")
    @NotBlank(message = "bizNumber")
    private String bizNumber;

    @Column(name = "CEO_NAME")
    @NotBlank(message = "ceoName")
    private String ceo;

    @Column(name = "PHONE_NUMBER")
    @NotBlank(message = "phoneNumber")
    private String phoneNumber;

    @Column(name = "ADDRESS")
    @NotBlank(message = "address")
    private String address;

    @Column(name = "COMPANY_SITE")
    @NotBlank(message = "companySite")
    private String companySite;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "SERVICE_USE_AGREE")
    private String serviceUseAgree;

    @Override
    public Object[] getEqualityFields() {
        return new Object[]{siteName, companyName, businessType, bizNumber, ceo, phoneNumber, address, companySite, email, serviceUseAgree};
    }

    //Stream.of: 세 개의 필드를 스트림으로 생성합니다.
    //map(Objects::requireNonNull): 각 필드가 null이 아닌지 확인합니다.
    //collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)): 리스트를 생성한 후, 이를 불변 리스트로 변환합니다.
    public List<String> clientInfo() {
        return Stream.of(companyName, bizNumber, ceo)
                .map(Objects::requireNonNull)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    @Builder
    public AgencyCompany(String siteName, String companyName, String businessType, String bizNumber, String ceo, String phoneNumber, String address, String companySite, String email, String serviceUseAgree) {
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
