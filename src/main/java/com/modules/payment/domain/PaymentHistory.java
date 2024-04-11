package com.modules.payment.domain;

import com.modules.payment.application.enums.EnumExtraAmountStatus;
import com.modules.payment.application.enums.EnumTradeTrace;
import com.modules.payment.domain.entity.PaymentJpaEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

import java.text.SimpleDateFormat;
import java.util.Date;

@ToString
@AllArgsConstructor
@Builder
public class PaymentHistory {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMdd");

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

    public String agencyId() {
        return this.agencyId;
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

    public Date endDate(){
        return this.endDate;
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

    public String chainSiteId(){
        return this.agencyId + "-" + this.siteId;
    }

    public String convertStartDate(){
        return FORMAT.format(this.startDate);
    }

    public String convertEndDate(){
        return FORMAT.format(this.endDate);
    }

    public String rateSel() {
        return this.rateSel;
    }


    public int calculateExcessCount(long useCount){
        int offer = Integer.parseInt(this.offer);
        int excessCount = offer - (int) useCount;
        return excessCount < 0 ? Math.abs(excessCount) : 0;
    }

    public PaymentJpaEntity toEntity(){
        return PaymentJpaEntity.builder()
                .tradeNum(this.tradeNum)
                .pgTradeNum(this.pgTradeNum)
                .agencyId(this.agencyId)
                .siteId(this.siteId)
                .paymentType(this.paymentType)
                .rateSel(this.rateSel)
                .amount(this.amount)
                .offer(this.offer)
                .useCount(this.useCount)
                .trTrace(this.trTrace)
                .paymentStatus(this.paymentStatus)
                .trDate(this.trDate)
                .startDate(this.startDate)
                .endDate(this.endDate)
                .rcptName(this.rcptName)
                .billKey(this.billKey)
                .billKeyExpireDate(this.billKeyExpireDate)
                .vbankName(this.vbankName)
                .vbankAccount(this.vbankAccount)
                .vbankExpireDate(this.vbankExpireDate)
                .regDate(this.regDate)
                .modDate(this.modDate)
                .extraAmountStatus(this.extraAmountStatus)
                .memo(this.memo)
                .build();
    }
}
