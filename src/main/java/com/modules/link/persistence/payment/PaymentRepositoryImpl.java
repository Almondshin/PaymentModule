package com.modules.link.persistence.payment;

import com.modules.base.jpa.BaseRepository;
import com.modules.link.domain.agency.SiteId;
import com.modules.link.domain.payment.PGTradeNum;
import com.modules.link.domain.payment.Payment;
import com.modules.link.domain.payment.PaymentRepository;
import com.modules.link.domain.payment.StatDay;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
class PaymentRepositoryImpl extends BaseRepository<Payment, PGTradeNum, PaymentJpaRepository> implements PaymentRepository {

    private final StatDayJpaRepository statDayRepository;
    public PaymentRepositoryImpl(PaymentJpaRepository repository, StatDayJpaRepository statDayRepository) {
        super(repository);
        this.statDayRepository = statDayRepository;
    }

    @Transactional
    @Override
    public List<Payment> findBySiteId(SiteId siteId) {
        return repository.findBySiteId(siteId);
    }

    @Transactional
    @Override
    public List<StatDay> findAllByFromDateBetweenAndId(String startDate, String endDate, SiteId siteId) {
        System.out.println("시작일 종료일 검증 : "+  startDate + " " + endDate);
        return statDayRepository.findAllByFromDateBetweenAndId(startDate, endDate, siteId);
    }

}
