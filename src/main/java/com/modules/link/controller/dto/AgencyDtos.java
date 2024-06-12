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
    public static class RegisterInfo {
        private SiteId siteId;
        private AgencyId agencyId;
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
        private Date startDate;
        private String settleManagerName;
        private String settleManagerPhoneNumber;
        private String settleManagerTelNumber;
        private String settleManagerEmail;
        private String serviceUseAgree;
    }


    @Getter
    public static class CancelInfo {
        private AgencyId agencyId;
        private SiteId siteId;
        private String description;
    }
}
