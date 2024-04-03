package com.modules.adapter.out.persistence.repository;

import com.modules.adapter.out.persistence.entity.PaymentJpaEntity;
import com.modules.adapter.out.persistence.repository.querydsl.PaymentHistoryRepositoryQueryDSL;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentHistoryRepository extends JpaRepository<PaymentJpaEntity, String>, PaymentHistoryRepositoryQueryDSL {
    Optional<PaymentJpaEntity> findByTradeNum(String tradeNum);
}
