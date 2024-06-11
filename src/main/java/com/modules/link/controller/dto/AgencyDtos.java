package com.modules.link.controller.dto;

import com.modules.link.domain.agency.*;
import lombok.Getter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

public class AgencyDtos {
    @Getter
    public static class StatusInfo {
        private AgencyId agencyId;
        private SiteId siteId;
    }

    @Getter
    @Validated
    public static class RegisterInfo {
        @NotNull(message = "siteId")
        private SiteId siteId;
        @NotNull(message = "agencyId")
        private AgencyId agencyId;
        @NotBlank(message = "siteName")
        private String siteName;
        @NotBlank(message = "companyName")
        private String companyName;
        @NotBlank(message = "businessType")
        private String businessType;
        @NotBlank(message = "bizNumber")
        private String bizNumber;
        @NotBlank(message = "ceoName")
        private String ceoName;
        @NotBlank(message = "phoneNumber")
        private String phoneNumber;
        @NotBlank(message = "address")
        private String address;
        @NotBlank(message = "companySite")
        private String companySite;
        private String email;
        private String rateSel;
        private Date startDate;
        @NotBlank(message = "settleManagerName")
        private String settleManagerName;
        @NotBlank(message = "settleManagerPhoneNumber")
        private String settleManagerPhoneNumber;
        @NotBlank(message = "settleManagerTelNumber")
        private String settleManagerTelNumber;
        @NotBlank(message = "settleManagerEmail")
        private String settleManagerEmail;
        private String serviceUseAgree;

        public Agency toAgency() {
            return Agency.of(
                    this.siteId,
                    this.agencyId,
                    AgencyCompany.builder()
                            .siteName(this.siteName)
                            .companyName(this.companyName)
                            .businessType(this.businessType)
                            .bizNumber(this.bizNumber)
                            .ceo(this.ceoName)
                            .phoneNumber(this.phoneNumber)
                            .address(this.address)
                            .companySite(this.companySite)
                            .email(this.email)
                            .serviceUseAgree(this.serviceUseAgree)
                            .build(),
                    AgencyManager.builder()
                            .name(this.settleManagerName)
                            .telNumber(this.settleManagerTelNumber)
                            .email(this.settleManagerEmail)
                            .phoneNumber(this.settleManagerPhoneNumber)
                            .build()
            );
        }
    }


    @Getter
    public static class CancelInfo {
        private AgencyId agencyId;
        private SiteId siteId;
    }
}
