package com.modules.payment.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "AGENCY_INFO_KEY")
public class AgencyInfoKeyJpaEntity {

  @Id
  @Column(name = "AGENCY_ID")
  private String agencyId;
  @Column(name = "AGENCY_NAME")
  private String agencyName;
  @Column(name = "AGENCY_PRODUCT_TYPE")
  private String agencyProductType;
  @Column(name = "AGENCY_URL")
  private String agencyUrl;
  @Column(name = "AGENCY_KEY")
  private String agencyKey;
  @Column(name = "AGENCY_IV")
  private String agencyIv;
  @Column(name = "REG_DATE")
  private Date regDate;
  @Column(name = "MOD_DATE")
  private Date modDate;
  @Column(name = "BILLING_BASE")
  private String billingBase;

}
