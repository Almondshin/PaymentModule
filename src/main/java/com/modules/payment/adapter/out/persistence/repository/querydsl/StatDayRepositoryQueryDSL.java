package com.modules.payment.adapter.out.persistence.repository.querydsl;

import com.modules.payment.adapter.out.persistence.entity.StatDayJpaEntity;

import java.util.List;

public interface StatDayRepositoryQueryDSL {
    List<StatDayJpaEntity> findBySiteIdAndFromDate(String siteId, String toDate, String fromDate);
}
