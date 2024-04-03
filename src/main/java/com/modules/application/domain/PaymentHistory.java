package com.modules.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@Getter
@ToString
@Builder
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


    public PaymentHistory(String tradeNum, String pgTradeNum, String agencyId, String siteId, String paymentType, String rateSel, String amount, String offer, String trTrace, String paymentStatus, Date trDate, Date startDate, Date endDate, String billKey, String billKeyExpireDate, Date regDate, String extraAmountStatus) {
        this.tradeNum = tradeNum;
        this.pgTradeNum = pgTradeNum;
        this.agencyId = agencyId;
        this.siteId = siteId;
        this.paymentType = paymentType;
        this.rateSel = rateSel;
        this.amount = amount;
        this.offer = offer;
        this.trTrace = trTrace;
        this.paymentStatus = paymentStatus;
        this.trDate = trDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.billKey = billKey;
        this.billKeyExpireDate = billKeyExpireDate;
        this.regDate = regDate;
        this.extraAmountStatus = extraAmountStatus;
    }

    public PaymentHistory(String tradeNum, String pgTradeNum, String agencyId, String siteId, String paymentType, String rateSel, String amount, String offer, String trTrace, String paymentStatus, Date trDate, Date startDate, Date endDate, Date regDate, String extraAmountStatus) {
        this.tradeNum = tradeNum;
        this.pgTradeNum = pgTradeNum;
        this.agencyId = agencyId;
        this.siteId = siteId;
        this.paymentType = paymentType;
        this.rateSel = rateSel;
        this.amount = amount;
        this.offer = offer;
        this.trTrace = trTrace;
        this.paymentStatus = paymentStatus;
        this.trDate = trDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.regDate = regDate;
        this.extraAmountStatus = extraAmountStatus;
    }


    public PaymentHistory(String tradeNum, String pgTradeNum, String agencyId, String siteId, String paymentType, String rateSel, String amount, String offer, String trTrace, String paymentStatus, Date trDate, String rcptName, String vbankName, String vbankCode, String vbankAccount, Date vbankExpireDate, Date startDate, Date endDate, Date regDate, Date modDate, String extraAmountStatus) {
        this.tradeNum = tradeNum;
        this.pgTradeNum = pgTradeNum;
        this.agencyId = agencyId;
        this.siteId = siteId;
        this.paymentType = paymentType;
        this.rateSel = rateSel;
        this.amount = amount;
        this.offer = offer;
        this.trTrace = trTrace;
        this.paymentStatus = paymentStatus;
        this.trDate = trDate;
        this.rcptName = rcptName;
        this.vbankName = vbankName;
        this.vbankCode = vbankCode;
        this.vbankAccount = vbankAccount;
        this.vbankExpireDate = vbankExpireDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.regDate = regDate;
        this.modDate = modDate;
        this.extraAmountStatus = extraAmountStatus;
    }

}
