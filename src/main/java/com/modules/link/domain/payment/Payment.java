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
import java.time.LocalDateTime;

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

    @Column(name = "BILL_KEY_EXPIREDATE")
    private String billKeyExpireDate;

    @Embedded
    private PaymentDetails paymentDetails;

    @Embedded
    private PaymentPeriod paymentPeriod;

    @Embedded
    private VBank vBank;

    @Column(name = "REG_DATE")
    private LocalDateTime regDate;

    @Column(name = "MOD_DATE")
    private LocalDateTime modDate;

    @Builder
    private Payment(PGTradeNum id, AgencyId agencyId, SiteId siteId, RateSel rateSel, String billKey, String billKeyExpireDate, PaymentDetails paymentDetails, PaymentPeriod paymentPeriod, VBank vBank, LocalDateTime regDate, LocalDateTime modDate) {
        this.id = id;
        this.agencyId = agencyId;
        this.siteId = siteId;
        this.rateSel = rateSel;
        this.billKey = billKey;
        this.billKeyExpireDate = billKeyExpireDate;
        this.paymentDetails = paymentDetails;
        this.paymentPeriod = paymentPeriod;
        this.vBank = vBank;
        this.regDate = regDate;
        this.modDate = modDate;
    }

    public static Payment ofCA(PGTradeNum id, AgencyId agencyId, SiteId siteId, RateSel rateSel,PaymentDetails paymentDetails, PaymentPeriod paymentPeriod, String billKey, String billKeyExpireDate) {
        if (id == null || agencyId == null || siteId == null) {
            throw new IllegalArgumentException("PGTradeNum, AgencyId, and SiteId cannot be null");
        }
        if (billKey == null) {
            billKey = "";
        }
        if (billKeyExpireDate == null) {
            billKeyExpireDate = "";
        }
        return Payment.builder()
                .id(id)
                .agencyId(agencyId)
                .siteId(siteId)
                .rateSel(rateSel)
                .paymentDetails(paymentDetails)
                .paymentPeriod(paymentPeriod)
                .billKey(billKey)
                .billKeyExpireDate(billKeyExpireDate)
                .regDate(LocalDateTime.now())
                .build();
    }

    public static Payment ofVAPending(PGTradeNum id, AgencyId agencyId, SiteId siteId, RateSel rateSel, PaymentDetails paymentDetails, PaymentPeriod paymentPeriod, VBank vBank) {
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
                .vBank(vBank)
                .regDate(LocalDateTime.now())
                .build();
    }
    public static Payment ofVA(Payment payment, PaymentDetails paymentDetails) {
        return Payment.builder()
                .id(payment.getId())
                .agencyId(payment.getAgencyId())
                .siteId(payment.getSiteId())
                .rateSel(payment.getRateSel())
                .billKey(payment.getBillKey())
                .paymentDetails(paymentDetails)
                .paymentPeriod(payment.getPaymentPeriod())
                .vBank(payment.getVBank())
                .regDate(payment.getRegDate())
                .modDate(LocalDateTime.now())
                .build();
    }

    public static Payment updatePaymentUseCount(Payment payment, PaymentDetails paymentDetails, int useCount) {
        return Payment.builder()
                .id(payment.getId())
                .agencyId(payment.getAgencyId())
                .siteId(payment.getSiteId())
                .rateSel(payment.getRateSel())
                .billKey(payment.getBillKey())
                .paymentDetails(PaymentDetails.updateUseCount(paymentDetails, String.valueOf(useCount)))
                .paymentPeriod(payment.getPaymentPeriod())
                .vBank(payment.getVBank())
                .regDate(payment.getRegDate())
                .modDate(LocalDateTime.now())
                .build();
    }
}
