package com.modules.payment.application.mapper;

import com.modules.payment.domain.PaymentHistory;
import com.modules.payment.domain.entity.PaymentJpaEntity;

public class PaymentHistoryMapper {

    public static PaymentHistory convertToDomain(PaymentJpaEntity entity) {
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
