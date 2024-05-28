package com.modules.link.domain.payment;

import com.modules.base.domain.DomainEntity;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Entity
public class Products extends DomainEntity<Products, String> {

    @Id
    @Column(name = "RATE_SEL")
    private String id;
    private String name;
    private String price;
    private String offer;
    private String month;
    private String feePerCase;
    private String excessPerCase;
}
