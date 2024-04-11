package com.modules.payment.adapter.out.persistence.repository.querydsl;

import com.modules.payment.domain.entity.PaymentJpaEntity;

import javax.validation.constraints.NotBlank;
import java.util.List;

public interface PaymentHistoryRepositoryQueryDSL {
    List<PaymentJpaEntity> findByAgencyIdAndSiteIdAndTrTrace(
            @NotBlank String agencyId,
            @NotBlank String siteId,
            @NotBlank String trTrace
    );
}
