package com.modules.payment.adapter.out.persistence.repository.querydsl;

import com.modules.payment.domain.entity.StatDayJpaEntity;

import java.util.List;

public interface StatDayRepositoryQueryDSL {
    List<StatDayJpaEntity> findBySiteIdAndFromDate(String siteId, String toDate, String fromDate);
}
