package com.modules.payment.adapter.out.persistence.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Table(name = "AGENCY_PAYMENT_HISTORY")
@Data
public class PaymentJpaEntity {

    @NotBlank
    @Column(name = "TRADE_NUM", nullable = false)
    private String tradeNum;
    @Id
    @NotBlank
    @Column(name = "PG_TRADE_NUM", nullable = false)
    private String pgTradeNum;
    @NotBlank
    @Column(name = "AGENCY_ID", nullable = false)
    private String agencyId;
    @NotBlank
    @Column(name = "SITE_ID", nullable = false)
    private String siteId;

    @Column(name = "PAYMENT_TYPE")
    private String paymentType;
    @Column(name = "RATE_SEL")
    private String rateSel;
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
    @Column(name = "START_DATE", nullable = false)
    private Date startDate;
    @Column(name = "END_DATE", nullable = false)
    private Date endDate;

    @Column(name = "RCPT_NAME")
    private String rcptName;

    @Column(name = "BILL_KEY")
    private String billKey;
    @Column(name = "BILL_KEY_EXPIREDATE")
    private String billKeyExpireDate;

    @Column(name = "VBANK_NAME")
    private String vbankName;
    @Column(name = "VBANK_ACCOUNT")
    private String vbankAccount;
    @Column(name = "VBANK_EXPIREDATE")
    private Date vbankExpireDate;

    @Column(name = "REG_DATE")
    private Date regDate;
    @Column(name = "MOD_DATE")
    private Date modDate;

    @Column(name = "EXTRA_AMOUNT_STATUS")
    private String extraAmountStatus;
    @Column(name = "MEMO")
    private String memo;

}
