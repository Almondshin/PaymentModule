package com.modules.link.service.agency;

import com.modules.link.domain.agency.*;
import com.modules.link.enums.EnumResultCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class AgencyDtos {
    @Getter
    public static class StatusInfo {
        private String agencyId;
        private String siteId;
    }

    @Getter
    @Validated
    public static class RegisterInfo {
        @NotNull(message = "siteId")
        private SiteId siteId;
        @NotNull(message = "agencyId")
        private AgencyId agencyId;
        @NotNull(message = "siteName")
        private String siteName;
        @NotNull(message = "companyName")
        private String companyName;
        @NotNull(message = "businessType")
        private String businessType;
        @NotNull(message = "bizNumber")
        private String bizNumber;
        @NotNull(message = "ceoName")
        private String ceoName;
        @NotNull(message = "phoneNumber")
        private String phoneNumber;
        @NotNull(message = "address")
        private String address;
        @NotNull(message = "companySite")
        private String companySite;
        private String email;
        private String rateSel;
        private Date startDate;
        @NotNull(message = "settleManagerName")
        private String settleManagerName;
        @NotNull(message = "settleManagerPhoneNumber")
        private String settleManagerPhoneNumber;
        @NotNull(message = "settleManagerTelNumber")
        private String settleManagerTelNumber;
        @NotNull(message = "settleManagerEmail")
        private String settleManagerEmail;
        @NotNull(message = "serviceUseAgree")
        private String serviceUseAgree;

        public Agency toAgency() {
            return Agency.of(
                    this.siteId,
                    this.agencyId,
                    AgencyCompany.builder()
                            .siteName(this.siteName)
                            .companyName(this.companyName)
                            .businessType(this.businessType)
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
        private String agencyId;
        private String siteId;
    }

    @Getter
    @ToString
    public static class AgencyResponse {

        private String resultCode;
        private String resultMsg;
        private String msgType;
        private String encryptData;
        private String verifyInfo;

        public AgencyResponse(){
            this.resultCode = EnumResultCode.SUCCESS.getCode();
            this.resultMsg = EnumResultCode.SUCCESS.getMessage();
        }
        public AgencyResponse(String encryptData, String verifyInfo, String messageType) {
            this.resultCode = EnumResultCode.SUCCESS.getCode();
            this.resultMsg = EnumResultCode.SUCCESS.getMessage();
            this.msgType = messageType;
            this.encryptData = encryptData;
            this.verifyInfo = verifyInfo;
        }

        public AgencyResponse(EnumResultCode resultCode) {
            this.resultCode = resultCode.getCode();
            this.resultMsg = resultCode.getMessage();
        }

        public AgencyResponse(String code, String message) {
            this.resultCode = code;
            this.resultMsg = message;
        }


    }
}
