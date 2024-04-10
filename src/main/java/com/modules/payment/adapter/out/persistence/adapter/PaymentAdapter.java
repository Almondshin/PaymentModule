package com.modules.payment.adapter.out.persistence.adapter;

import com.modules.payment.adapter.out.persistence.entity.PaymentJpaEntity;
import com.modules.payment.adapter.out.persistence.repository.PaymentHistoryRepository;
import com.modules.payment.application.domain.Agency;
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
        String siteId = agency.getAgencyId() + "-" + agency.getSiteId();
        List<PaymentJpaEntity> foundPaymentHistory = paymentHistoryRepository.findByAgencyIdAndSiteIdAndTrTrace(agency.getAgencyId(), siteId, EnumTradeTrace.USED.getCode());
        if (foundPaymentHistory == null || foundPaymentHistory.isEmpty()) {
            throw new EntityNotFoundException("Agency siteId : " + agency.getSiteId() + " EnumTradeTrace : " + EnumTradeTrace.USED.getCode() + " 인 엔터티를 찾을 수 없습니다.");
        } else {
            return foundPaymentHistory.stream()
                    .map(this::convertToDomain)
                    .sorted(Comparator.comparing(PaymentHistory::getEndDate).reversed())
                    .collect(Collectors.toList());
        }
    }


    @Override
    @Transactional
    public Optional<PaymentHistory> getPaymentHistoryByTradeNum(String pgTradeNum) {
        Optional<PaymentJpaEntity> entity = paymentHistoryRepository.findById(pgTradeNum);
        return entity.map(this::convertToDomain);
    }

    @Override
    @Transactional
    public void insertPayment(PaymentHistory paymentHistory) {
        PaymentJpaEntity entity = convertToEntity(paymentHistory);
        paymentHistoryRepository.save(entity);
    }


    @Override
    @Transactional
    public void updatePayment(PaymentHistory paymentHistory) {
        //persistence context
        Optional<PaymentJpaEntity> optionalEntity = paymentHistoryRepository.findById(paymentHistory.getPgTradeNum());
        if (optionalEntity.isPresent()) {
            updateEntityFields(paymentHistory, optionalEntity.get());
        } else {
            throw new EntityNotFoundException("hfTradeNum : " + paymentHistory.getPgTradeNum() + "인 엔터티를 찾을 수 없습니다.");
        }
    }

    @Override
    @Transactional
    public void updatePaymentUseCount(String tradeNum, String pgTradeNum, long useCountSum) {
        Optional<PaymentJpaEntity> optionalEntity = paymentHistoryRepository.findById(pgTradeNum);
        if (optionalEntity.isPresent()) {
            PaymentJpaEntity entity = optionalEntity.get();
            entity.setUseCount(Long.toString(useCountSum));
            if (!Objects.equals(entity.getTradeNum(), tradeNum)) {
                throw new EntityNotFoundException("tradeNum : " + entity.getTradeNum() + "인 엔터티를 찾을 수 없습니다.");
            }
        }

    }

    @Override
    @Transactional
    public void updatePaymentExtraAmountStatus(PaymentHistory paymentHistory) {
        Optional<PaymentJpaEntity> optionalEntity = paymentHistoryRepository.findById(paymentHistory.getPgTradeNum());
        if (optionalEntity.isPresent()){
            PaymentJpaEntity entity = optionalEntity.get();
            entity.setExtraAmountStatus(EnumExtraAmountStatus.SYSTEM_COMPLETE.getCode());
        }
    }


    private void updateEntityFields(PaymentHistory paymentHistory, PaymentJpaEntity paymentJpaEntity) {
        paymentJpaEntity.setTradeNum(paymentHistory.getTradeNum());
        paymentJpaEntity.setPgTradeNum(paymentHistory.getPgTradeNum());
        paymentJpaEntity.setAgencyId(paymentHistory.getAgencyId());
        paymentJpaEntity.setSiteId(paymentHistory.getAgencyId() + "-" + paymentHistory.getSiteId());
        paymentJpaEntity.setPaymentType(paymentHistory.getPaymentType());
        paymentJpaEntity.setRateSel(paymentHistory.getRateSel());
        paymentJpaEntity.setAmount(paymentHistory.getAmount());
        paymentJpaEntity.setOffer(paymentHistory.getOffer());
        paymentJpaEntity.setTrTrace(paymentHistory.getTrTrace());
        paymentJpaEntity.setPaymentStatus(paymentHistory.getPaymentStatus());
        paymentJpaEntity.setTrDate(paymentHistory.getTrDate());
        paymentJpaEntity.setStartDate(paymentHistory.getStartDate());
        paymentJpaEntity.setEndDate(paymentHistory.getEndDate());
        paymentJpaEntity.setRcptName(paymentHistory.getRcptName());
        paymentJpaEntity.setVbankName(paymentHistory.getVbankName());
        paymentJpaEntity.setVbankAccount(paymentHistory.getVbankAccount());
        paymentJpaEntity.setVbankExpireDate(paymentHistory.getVbankExpireDate());
        paymentJpaEntity.setRegDate(paymentHistory.getRegDate());
        paymentJpaEntity.setModDate(paymentHistory.getModDate());
    }

    private PaymentJpaEntity convertToEntity(PaymentHistory paymentHistory) {
        PaymentJpaEntity entity = new PaymentJpaEntity();
        entity.setTradeNum(paymentHistory.getTradeNum());
        entity.setPgTradeNum(paymentHistory.getPgTradeNum());
        entity.setAgencyId(paymentHistory.getAgencyId());
        entity.setSiteId(paymentHistory.getAgencyId() + "-" + paymentHistory.getSiteId());
        entity.setPaymentType(paymentHistory.getPaymentType());
        entity.setRateSel(paymentHistory.getRateSel());
        entity.setAmount(paymentHistory.getAmount());
        entity.setOffer(paymentHistory.getOffer());
        entity.setUseCount(paymentHistory.getUseCount());
        entity.setTrTrace(paymentHistory.getTrTrace());
        entity.setPaymentStatus(paymentHistory.getPaymentStatus());
        entity.setTrDate(paymentHistory.getTrDate());
        entity.setStartDate(paymentHistory.getStartDate());
        entity.setEndDate(paymentHistory.getEndDate());
        entity.setRcptName(paymentHistory.getRcptName());
        entity.setBillKey(paymentHistory.getBillKey());
        entity.setBillKeyExpireDate(paymentHistory.getBillKeyExpireDate());
        entity.setVbankName(paymentHistory.getVbankName());
        entity.setVbankAccount(paymentHistory.getVbankAccount());
        entity.setVbankExpireDate(paymentHistory.getVbankExpireDate());
        entity.setRegDate(paymentHistory.getRegDate());
        entity.setModDate(paymentHistory.getModDate());
        entity.setExtraAmountStatus(paymentHistory.getExtraAmountStatus());
        entity.setMemo(paymentHistory.getMemo());
        return entity;
    }

    private PaymentHistory convertToDomain(PaymentJpaEntity entity) {
        return PaymentHistory.builder()
                .tradeNum(entity.getTradeNum())
                .pgTradeNum(entity.getPgTradeNum())
                .agencyId(entity.getAgencyId())
                .siteId(entity.getSiteId().split("-")[1])
                .paymentType(entity.getPaymentType())
                .rateSel(entity.getRateSel())
                .amount(entity.getAmount())
                .offer(entity.getOffer())
                .useCount(entity.getUseCount())
                .trTrace(entity.getTrTrace())
                .paymentStatus(entity.getPaymentStatus())
                .trDate(entity.getTrDate())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .rcptName(entity.getRcptName())
                .billKey(entity.getBillKey())
                .billKeyExpireDate(entity.getBillKeyExpireDate())
                .vbankName(entity.getVbankName())
                .vbankAccount(entity.getVbankAccount())
                .vbankExpireDate(entity.getVbankExpireDate())
                .regDate(entity.getRegDate())
                .modDate(entity.getModDate())
                .extraAmountStatus(entity.getExtraAmountStatus())
                .memo(entity.getMemo())
                .build();
    }

}


