package com.modules.payment.application.mapper;

import com.modules.payment.domain.AgencyInfoKey;
import com.modules.payment.domain.entity.AgencyInfoKeyJpaEntity;

public class AgencyInfoKeyMapper {

    public static AgencyInfoKey convertDomain(AgencyInfoKeyJpaEntity entity) {
        return AgencyInfoKey.builder()
                .agencyId(entity.getAgencyId())
                .agencyName(entity.getAgencyName())
                .agencyProductType(entity.getAgencyProductType())
                .agencyUrl(entity.getAgencyUrl())
                .agencyKey(entity.getAgencyKey())
                .agencyIv(entity.getAgencyIv())
                .billingBase(entity.getBillingBase())
                .build();
    }
}
