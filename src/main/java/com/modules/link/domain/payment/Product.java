package com.modules.link.domain.payment;

import com.modules.base.domain.DomainEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Entity
@NoArgsConstructor
public class Product extends DomainEntity<Product, RateSel> {

    @Id
    @Type(type = "com.modules.link.domain.payment.RateSel$RateSelJavaType")
    @Column(name = "RATE_SEL")
    private RateSel id;
    @Column(name = "AGENCY_ID")
    private String agencyId;
    @Column(name = "NAME")
    private String name;
    @Column(name = "PRICE")
    private String price;
    @Column(name = "OFFER")
    private String offer;
    @Column(name = "MONTH")
    private String month;
    @Column(name = "FEE_PER_CASE")
    private String feePerCase;
    @Column(name = "EXCESS_PER_CASE")
    private String excessPerCase;
}
