package com.modules.payment.domain;

import com.modules.payment.application.enums.EnumExtraAmountStatus;
import com.modules.payment.application.enums.EnumTradeTrace;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.Date;

@ToString
@AllArgsConstructor
public class PaymentHistory {
    private String tradeNum;
    private String pgTradeNum;
    private String agencyId;
    private String siteId;

    private String paymentType;
    private String rateSel;
    private String amount;
    private String offer;
    private String useCount;
    private String trTrace;
    private String paymentStatus;

    private Date trDate;
    private Date startDate;
    private Date endDate;

    private String rcptName;

    private String billKey;
    private String billKeyExpireDate;

    private String vbankName;
    private String vbankCode;
    private String vbankAccount;
    private Date vbankExpireDate;

    private Date regDate;
    private Date modDate;

    private String extraAmountStatus;
    private String memo;


    public boolean isPassed() {
        return this.trTrace.equals(EnumTradeTrace.USED.getCode())
                && this.extraAmountStatus.equals(EnumExtraAmountStatus.PASS.getCode());
    }

    public String retrieveBillKey() {
        return this.billKey;
    }

    public boolean isSameTradeNum(String tradeNum) {
        return this.tradeNum.equals(tradeNum);
    }

    public boolean isScheduledRateSel() {
        return this.rateSel.toLowerCase().contains("autopay");
    }

    public String tradeNum() {
        if (!isValidData()) {
            throw new IllegalArgumentException("Invalid data : " + this.agencyId + ", " + this.siteId + ", " + this.tradeNum);
        }
        return this.tradeNum;
    }

    public String paymentAmount() {
        return this.amount;
    }


    private boolean isValidData() {
        return this.agencyId != null && this.siteId != null && this.tradeNum != null;
    }

    public PGDataContainer pgDataContainer(String type, String mchtId, String tradeNum, String billKey, String amount, String productName) {
        return new PGDataContainer(type, mchtId, tradeNum, billKey, amount, productName);
    }

    public PGDataContainer pgDataContainer(String type, String mchtId, String tradeNum, String amount) {
        return new PGDataContainer(type, mchtId, tradeNum, amount);
    }


}
