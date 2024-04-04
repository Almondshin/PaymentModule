package com.modules.adapter.in.models;

import com.modules.application.enums.EnumExtraAmountStatus;
import com.modules.application.enums.EnumTradeTrace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@ToString
@Builder
@AllArgsConstructor
public class PaymentHistoryDataContainer {
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


    public boolean isActiveTradeTraceAndPassExtraAmountStatus() {
        return this.trTrace.equals(EnumTradeTrace.USED.getCode())
                && this.extraAmountStatus.equals(EnumExtraAmountStatus.PASS.getCode());
    }

    public String retrieveBillKey() {
        return this.billKey;
    }





}
