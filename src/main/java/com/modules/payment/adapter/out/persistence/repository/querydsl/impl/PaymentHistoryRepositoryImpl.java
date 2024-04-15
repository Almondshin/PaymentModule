package com.modules.payment.adapter.out.persistence.repository.querydsl.impl;

import com.modules.payment.domain.entity.PaymentJpaEntity;
import com.modules.payment.adapter.out.persistence.repository.querydsl.PaymentHistoryRepositoryQueryDSL;
import com.modules.payment.domain.entity.QPaymentJpaEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentHistoryRepositoryImpl implements PaymentHistoryRepositoryQueryDSL {

    private final JPAQueryFactory jpaQueryFactory;
    QPaymentJpaEntity qPaymentJpaEntity = QPaymentJpaEntity.paymentJpaEntity;

    @Override
    public List<PaymentJpaEntity> findByAgencyIdAndSiteIdAndTrTrace(String agencyId, String siteId, String trTrace) {
        return jpaQueryFactory.selectFrom(qPaymentJpaEntity)
                .where(qPaymentJpaEntity.agencyId.eq(agencyId)
                        .and(qPaymentJpaEntity.siteId.eq(siteId))
                        .and(qPaymentJpaEntity.trTrace.eq(trTrace)))
                .orderBy(qPaymentJpaEntity.trDate.desc())
                .fetch();
    }
}
