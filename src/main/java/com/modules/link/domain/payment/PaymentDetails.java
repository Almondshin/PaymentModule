package com.modules.link.domain.payment;

import com.modules.base.domain.ValueObject;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Date;

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
    private Date trDate;

    @Column(name = "EXTRA_AMOUNT_STATUS")
    private String extraAmountStatus;

    @Column(name = "MEMO")
    private String memo;

    @Builder
    public PaymentDetails(String tradeNum, String paymentType, String amount, String offer, String useCount, String trTrace, String paymentStatus, Date trDate, String extraAmountStatus, String memo) {
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

    @Override
    protected Object[] getEqualityFields() {
        return new Object[]{tradeNum, paymentType, amount, offer, useCount, trTrace, paymentStatus, trDate, extraAmountStatus, memo};
    }
}