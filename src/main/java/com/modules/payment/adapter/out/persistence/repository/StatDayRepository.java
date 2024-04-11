package com.modules.payment.adapter.out.persistence.repository;

import com.modules.payment.domain.entity.StatDayJpaEntity;
import com.modules.payment.adapter.out.persistence.repository.querydsl.StatDayRepositoryQueryDSL;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatDayRepository  extends JpaRepository<StatDayJpaEntity, String>, StatDayRepositoryQueryDSL {

}
