package com.modules.link.domain.payment;

import com.modules.base.domain.ValueObject;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;

@Getter
@Embeddable
@NoArgsConstructor
public class PaymentDetails extends ValueObject<PaymentDetails> {

    @Column(name = "TRADE_NUM")
    private String tradeNum;

    @Column(name = "PAYMENT_TYPE")
    private String paymentType;

    @Column(name = "AMOUNT")
    private String amount;

    @Column(name = "OFFER")
    private String offer;

    @Column(name = "USE_COUNT")
    private String useCount;

    @Column(name = "TR_TRACE")
    private String trTrace;

    @Column(name = "PAYMENT_STATUS")
    private String paymentStatus;

    @Column(name = "TR_DATE")
    private LocalDate trDate;

    @Column(name = "EXTRA_AMOUNT_STATUS")
    private String extraAmountStatus;

    @Column(name = "MEMO")
    private String memo;

    @Builder
    public PaymentDetails(String tradeNum, String paymentType, String amount, String offer, String useCount, String trTrace, String paymentStatus, LocalDate trDate, String extraAmountStatus, String memo) {
        this.tradeNum = tradeNum;
        this.paymentType = paymentType;
        this.amount = amount;
        this.offer = offer;
        this.useCount = useCount;
        this.trTrace = trTrace;
        this.paymentStatus = paymentStatus;
        this.trDate = trDate;
        this.extraAmountStatus = extraAmountStatus;
        this.memo = memo;
    }

    public static PaymentDetails updateUseCount(PaymentDetails paymentDetails, String useCount) {
        return PaymentDetails.builder()
                .tradeNum(paymentDetails.getTradeNum())
                .paymentType(paymentDetails.getPaymentType())
                .amount(paymentDetails.getAmount())
                .offer(paymentDetails.getOffer())
                .useCount(useCount)
                .trTrace(paymentDetails.getTrTrace())
                .paymentStatus(paymentDetails.getPaymentStatus())
                .trDate(paymentDetails.getTrDate())
                .extraAmountStatus(paymentDetails.getExtraAmountStatus())
                .memo(paymentDetails.getMemo())
                .build();
    }


    @Override
    public Object[] getEqualityFields() {
        return new Object[]{tradeNum, paymentType, amount, offer, useCount, trDate, memo};
    }
}