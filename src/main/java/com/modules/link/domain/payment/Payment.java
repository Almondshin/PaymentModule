package com.modules.link.domain.payment;

import com.modules.base.domain.AggregateRoot;
import com.modules.link.domain.agency.SiteId;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@ToString
@NoArgsConstructor
@Table(name = "AGENCY_PAYMENT_HISTORY")
public class Payment extends AggregateRoot<Payment, PGTradeNum> {

    @Id
    @Type(type = "com.modules.link.domain.payment.PGTradeNum$PGTradeNumJavaType")
    @Column(name = "PG_TRADE_NUM", nullable = false)
    private PGTradeNum id;

    @Column(name = "AGENCY_ID", nullable = false)
    private String agencyId;

    @Type(type = "com.modules.link.domain.agency.SiteId$SiteIdJavaType")
    @Column(name = "SITE_ID", nullable = false)
    private SiteId siteId;

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

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "RATE_SEL", insertable = false, updatable = false)
    private final List<Product> products = new ArrayList<>();



    @Builder
    public Payment(PGTradeNum id, String agencyId, SiteId siteId, String billKey, PaymentDetails paymentDetails, PaymentPeriod paymentPeriod, VBank vBank, Date regDate, Date modDate) {
        this.id = id;
        this.agencyId = agencyId;
        this.siteId = siteId;
        this.billKey = billKey;
        this.paymentDetails = paymentDetails;
        this.paymentPeriod = paymentPeriod;
        this.vBank = vBank;
        this.regDate = regDate;
        this.modDate = modDate;
    }


    public static Payment of(PGTradeNum id, String agencyId, SiteId siteId, PaymentDetails paymentDetails, PaymentPeriod paymentPeriod, VBank vBank) {
        if (id == null || agencyId == null || siteId == null) {
            throw new IllegalArgumentException("PGTradeNum, AgencyId, and SiteId cannot be null");
        }
        return Payment.builder()
                .id(id)
                .agencyId(agencyId)
                .siteId(siteId)
                .billKey(null)
                .paymentDetails(paymentDetails)
                .paymentPeriod(paymentPeriod)
                .vBank(vBank)
                .regDate(new Date())
                .modDate(new Date())
                .build();
    }

    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }

}
