package com.modules.link.persistence.payment;

import com.modules.base.jpa.BaseRepository;
import com.modules.link.domain.agency.SiteId;
import com.modules.link.domain.payment.PGTradeNum;
import com.modules.link.domain.payment.Payment;
import com.modules.link.domain.payment.PaymentRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
class PaymentRepositoryImpl extends BaseRepository<Payment, PGTradeNum, PaymentJpaRepository> implements PaymentRepository {


    public PaymentRepositoryImpl(PaymentJpaRepository repository) {
        super(repository);
    }

    @Transactional
    @Override
    public List<Payment> findBySiteId(SiteId siteId) {
        return repository.findBySiteId(siteId);
    }
}
