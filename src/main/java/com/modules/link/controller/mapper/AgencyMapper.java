package com.modules.link.controller.mapper;

import com.modules.link.controller.dto.AgencyDtos.RegisterInfo;
import com.modules.link.domain.agency.Agency;
import com.modules.link.domain.agency.AgencyCompany;
import com.modules.link.domain.agency.AgencyManager;

public class AgencyMapper {
    public static Agency toAgency(RegisterInfo registerInfo) {
        return Agency.of(
                registerInfo.getSiteId(),
                registerInfo.getAgencyId(),
                AgencyCompany.builder()
                        .siteName(registerInfo.getSiteName())
                        .companyName(registerInfo.getCompanyName())
                        .businessType(registerInfo.getBusinessType())
                        .bizNumber(registerInfo.getBizNumber())
                        .ceo(registerInfo.getCeoName())
                        .phoneNumber(registerInfo.getPhoneNumber())
                        .address(registerInfo.getAddress())
                        .companySite(registerInfo.getCompanySite())
                        .email(registerInfo.getEmail())
                        .serviceUseAgree(registerInfo.getServiceUseAgree())
                        .build(),
                AgencyManager.builder()
                        .name(registerInfo.getSettleManagerName())
                        .phoneNumber(registerInfo.getSettleManagerPhoneNumber())
                        .telNumber(registerInfo.getSettleManagerTelNumber())
                        .email(registerInfo.getSettleManagerEmail())
                        .build()
        );
    }
}
