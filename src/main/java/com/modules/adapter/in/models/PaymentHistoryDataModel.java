package com.modules.adapter.in.models;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class PaymentHistoryDataModel {
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
}
