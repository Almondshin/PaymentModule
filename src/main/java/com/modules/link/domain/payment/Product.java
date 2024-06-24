package com.modules.link.domain.payment;

import com.modules.base.domain.AggregateRoot;
import com.modules.base.domain.DomainEntity;
import com.modules.link.domain.agency.AgencyId;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

@Getter
@Entity
@ToString
@NoArgsConstructor
@Table(name = "AGENCY_PRODUCTS")
public class Product extends DomainEntity<Product, RateSel> {

    @Id
    @Type(type = "com.modules.link.domain.payment.RateSel$RateSelJavaType")
    @Column(name = "RATE_SEL")
    private RateSel id;
    @Type(type = "com.modules.link.domain.agency.AgencyId$AgencyIdJavaType")
    @Column(name = "AGENCY_ID")
    private AgencyId agencyId;
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

    @Builder
    public Product(RateSel id, AgencyId agencyId, String name, String price, String offer, String month, String feePerCase, String excessPerCase) {
        this.id = id;
        this.agencyId = agencyId;
        this.name = name;
        this.price = price;
        this.offer = offer;
        this.month = month;
        this.feePerCase = feePerCase;
        this.excessPerCase = excessPerCase;
    }

    public Map<String, String> toMap() {
        Map<String, String> productMap = new HashMap<>();
        productMap.put("id", this.getId().toString());
        productMap.put("agencyId", this.getAgencyId().toString());
        productMap.put("name", this.getName());
        productMap.put("price", this.getPrice());
        productMap.put("offer", this.getOffer());
        productMap.put("month", this.getMonth());
        productMap.put("feePerCase", this.getFeePerCase());
        productMap.put("excessPerCase", this.getExcessPerCase());
        return productMap;
    }
}
