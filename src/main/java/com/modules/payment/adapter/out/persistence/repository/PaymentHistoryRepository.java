package com.modules.payment.adapter.out.persistence.repository;

import com.modules.payment.domain.entity.PaymentJpaEntity;
import com.modules.payment.adapter.out.persistence.repository.querydsl.PaymentHistoryRepositoryQueryDSL;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentHistoryRepository extends JpaRepository<PaymentJpaEntity, String>, PaymentHistoryRepositoryQueryDSL {
    Optional<PaymentJpaEntity> findByTradeNum(String tradeNum);
}
