package com.modules.link.persistence.payment;

import com.modules.link.domain.agency.SiteId;
import com.modules.link.domain.payment.PGTradeNum;
import com.modules.link.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface PaymentJpaRepository extends JpaRepository<Payment, PGTradeNum> {
    List<Payment> findBySiteId(SiteId siteId);
}
