package com.modules.payment.application.mapper;

import com.modules.payment.domain.entity.AgencyJpaEntity;
import com.modules.payment.domain.Agency;

public class AgencyMapper {

    public static Agency convertToAgency(AgencyJpaEntity entity){
        return Agency.builder()
                .agencyId(entity.getAgencyId())
                .siteId(entity.getSiteId().split("-")[1])
                .siteName(entity.getSiteName())
                .companyName(entity.getCompanyName())
                .businessType(entity.getBusinessType())
                .bizNumber(entity.getBizNumber())
                .ceoName(entity.getCeoName())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .companySite(entity.getCompanySite())
                .email(entity.getEmail())
                .rateSel(entity.getRateSel())
                .scheduledRateSel(entity.getScheduledRateSel())
                .siteStatus(entity.getSiteStatus())
                .extensionStatus(entity.getExtensionStatus())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .settleManagerName(entity.getSettleManagerName())
                .settleManagerPhoneNumber(entity.getSettleManagerPhoneNumber())
                .settleManagerTelNumber(entity.getSettleManagerTelNumber())
                .settleManagerEmail(entity.getSettleManagerEmail())
                .serviceUseAgree(entity.getServiceUseAgree())
                .build();
    }
}
