package com.modules.link.domain.payment;

import com.modules.base.domain.AggregateRoot;
import com.modules.link.domain.agency.AgencyId;
import com.modules.link.domain.agency.SiteId;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@ToString
@NoArgsConstructor
@Table(name = "AGENCY_PAYMENT_HISTORY")
public class Payment extends AggregateRoot<Payment, PGTradeNum> implements Serializable {

    @Id
    @Type(type = "com.modules.link.domain.payment.PGTradeNum$PGTradeNumJavaType")
    @Column(name = "PG_TRADE_NUM", nullable = false)
    private PGTradeNum id;

    @Type(type = "com.modules.link.domain.agency.AgencyId$AgencyIdJavaType")
    @Column(name = "AGENCY_ID", nullable = false)
    private AgencyId agencyId;

    @Type(type = "com.modules.link.domain.agency.SiteId$SiteIdJavaType")
    @Column(name = "SITE_ID", nullable = false)
    private SiteId siteId;

    @Type(type = "com.modules.link.domain.payment.RateSel$RateSelJavaType")
    @Column(name = "RATE_SEL")
    private RateSel rateSel;

    @Column(name = "BILL_KEY")
    private String billKey;

    @Embedded
    private PaymentDetails paymentDetails;

    @Embedded
    private PaymentPeriod paymentPeriod;

    @Embedded
    private VBank vBank;

    @Column(name = "REG_DATE")
    private Date regDate;

    @Column(name = "MOD_DATE")
    private Date modDate;

    @Builder
    private Payment(PGTradeNum id, AgencyId agencyId, SiteId siteId, RateSel rateSel, String billKey, PaymentDetails paymentDetails, PaymentPeriod paymentPeriod, VBank vBank, Date regDate, Date modDate) {
        this.id = id;
        this.agencyId = agencyId;
        this.siteId = siteId;
        this.rateSel = rateSel;
        this.billKey = billKey;
        this.paymentDetails = paymentDetails;
        this.paymentPeriod = paymentPeriod;
        this.vBank = vBank;
        this.regDate = regDate;
        this.modDate = modDate;
    }

    public static Payment ofAutoCA(PGTradeNum id, AgencyId agencyId, SiteId siteId, RateSel rateSel, String billKey, PaymentDetails paymentDetails, PaymentPeriod paymentPeriod) {
        if (id == null || agencyId == null || siteId == null) {
            throw new IllegalArgumentException("PGTradeNum, AgencyId, and SiteId cannot be null");
        }
        return Payment.builder()
                .id(id)
                .agencyId(agencyId)
                .siteId(siteId)
                .rateSel(rateSel)
                .billKey(billKey)
                .paymentDetails(paymentDetails)
                .paymentPeriod(paymentPeriod)
                .regDate(new Date())
                .modDate(new Date())
                .build();
    }

    public static Payment ofCA(PGTradeNum id, AgencyId agencyId, SiteId siteId, RateSel rateSel,PaymentDetails paymentDetails, PaymentPeriod paymentPeriod) {
        if (id == null || agencyId == null || siteId == null) {
            throw new IllegalArgumentException("PGTradeNum, AgencyId, and SiteId cannot be null");
        }
        return Payment.builder()
                .id(id)
                .agencyId(agencyId)
                .siteId(siteId)
                .rateSel(rateSel)
                .paymentDetails(paymentDetails)
                .paymentPeriod(paymentPeriod)
                .regDate(new Date())
                .modDate(new Date())
                .build();
    }

    public static Payment ofVA(PGTradeNum id, AgencyId agencyId, SiteId siteId, RateSel rateSel, PaymentDetails paymentDetails, PaymentPeriod paymentPeriod, VBank vBank) {
        if (id == null || agencyId == null || siteId == null) {
            throw new IllegalArgumentException("PGTradeNum, AgencyId, and SiteId cannot be null");
        }
        return Payment.builder()
                .id(id)
                .agencyId(agencyId)
                .siteId(siteId)
                .rateSel(rateSel)
                .paymentDetails(paymentDetails)
                .paymentPeriod(paymentPeriod)
                .regDate(new Date())
                .modDate(new Date())
                .vBank(vBank)
                .build();
    }
}
