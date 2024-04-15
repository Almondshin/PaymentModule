package com.modules.payment.adapter.out.persistence.adapter;

import com.modules.payment.application.mapper.PaymentHistoryMapper;
import com.modules.payment.domain.Agency;
import com.modules.payment.domain.entity.AgencyJpaEntity;
import com.modules.payment.domain.entity.PaymentJpaEntity;
import com.modules.payment.adapter.out.persistence.repository.PaymentHistoryRepository;
import com.modules.payment.application.enums.EnumExtraAmountStatus;
import com.modules.payment.application.enums.EnumTradeTrace;
import com.modules.payment.application.port.out.load.LoadPaymentDataPort;
import com.modules.payment.application.port.out.save.SavePaymentDataPort;
import com.modules.payment.domain.PaymentHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PaymentAdapter implements LoadPaymentDataPort, SavePaymentDataPort {

    private final PaymentHistoryRepository paymentHistoryRepository;

    public PaymentAdapter(PaymentHistoryRepository paymentHistoryRepository) {
        this.paymentHistoryRepository = paymentHistoryRepository;
    }

    @Override
    @Transactional
    public List<PaymentHistory> getPaymentHistoryByAgency(Agency agency) {
        AgencyJpaEntity entity = agency.toEntity();
        String siteId = entity.chainSiteId();
        String agencyId = entity.getAgencyId();
        String tradeTraceUsedCode = EnumTradeTrace.USED.getCode();

        List<PaymentJpaEntity> foundPaymentHistory = paymentHistoryRepository.findByAgencyIdAndSiteIdAndTrTrace(agencyId, siteId, tradeTraceUsedCode);

        return Optional.ofNullable(foundPaymentHistory)
                .filter(historyList -> !historyList.isEmpty())
                .orElseThrow(() -> new EntityNotFoundException("Agency siteId : " + siteId + " EnumTradeTrace : " + tradeTraceUsedCode + " 인 엔터티를 찾을 수 없습니다."))
                .stream()
                .map(PaymentHistoryMapper::convertToDomain)
                .sorted(Comparator.comparing(PaymentHistory::endDate).reversed())
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public Optional<PaymentHistory> getPaymentHistoryByTradeNum(String pgTradeNum) {
        Optional<PaymentJpaEntity> entity = paymentHistoryRepository.findById(pgTradeNum);
        return entity.map(PaymentHistoryMapper::convertToDomain);
    }

    @Override
    @Transactional
    public void insertPayment(PaymentHistory paymentHistory) {
        PaymentJpaEntity entity = paymentHistory.toEntity();
        paymentHistoryRepository.save(entity);
    }


    @Override
    @Transactional
    public void updatePayment(PaymentHistory paymentHistory) {
        //persistence context
        Optional<PaymentJpaEntity> optionalEntity = paymentHistoryRepository.findById(paymentHistory.pgTradeNum());
        if (optionalEntity.isPresent()) {
            paymentHistory.toEntity();
        } else {
            throw new EntityNotFoundException("hfTradeNum : " + paymentHistory.pgTradeNum() + "인 엔터티를 찾을 수 없습니다.");
        }
    }

    @Override
    @Transactional
    public void updatePaymentUseCount(PaymentHistory paymentHistory, long useCountSum) {
        PaymentJpaEntity entity = paymentHistory.toEntity();
        String pgTradeNum = entity.getPgTradeNum();
        String tradeNum = entity.getTradeNum();
        Optional<PaymentJpaEntity> optionalEntity = paymentHistoryRepository.findById(pgTradeNum);
        if (optionalEntity.isPresent()) {
            PaymentJpaEntity searchedEntity = optionalEntity.get();
            searchedEntity.setUseCount(Long.toString(useCountSum));
            if (!Objects.equals(searchedEntity.getTradeNum(), tradeNum)) {
                throw new EntityNotFoundException("tradeNum : " + entity.getTradeNum() + "인 엔터티를 찾을 수 없습니다.");
            }
        }

    }

    @Override
    @Transactional
    public void updatePaymentExtraAmountStatus(PaymentHistory paymentHistory) {
        Optional<PaymentJpaEntity> optionalEntity = paymentHistoryRepository.findById(paymentHistory.pgTradeNum());
        if (optionalEntity.isPresent()) {
            PaymentJpaEntity entity = optionalEntity.get();
            entity.setExtraAmountStatus(EnumExtraAmountStatus.SYSTEM_COMPLETE.getCode());
        }
    }
}


