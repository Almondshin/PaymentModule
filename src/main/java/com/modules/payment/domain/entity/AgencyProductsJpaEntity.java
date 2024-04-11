package com.modules.payment.domain.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AGENCY_PRODUCTS")
@Data
public class AgencyProductsJpaEntity {
  @Id
  @Column(name = "RATE_SEL")
  private String rateSel;
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
