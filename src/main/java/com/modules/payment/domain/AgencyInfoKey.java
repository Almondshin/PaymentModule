package com.modules.payment.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AgencyInfoKey {
  private String agencyId;
  private String agencyName;
  private String agencyProductType;
  private String agencyUrl;
  private String agencyKey;
  private String agencyIv;
  private Date regDate;
  private Date modDate;
  private String billingBase;
}
