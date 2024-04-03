package com.modules.adapter.out.persistence.repository;

import com.modules.adapter.out.persistence.entity.StatDayJpaEntity;
import com.modules.adapter.out.persistence.repository.querydsl.StatDayRepositoryQueryDSL;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatDayRepository  extends JpaRepository<StatDayJpaEntity, String>, StatDayRepositoryQueryDSL {

}
