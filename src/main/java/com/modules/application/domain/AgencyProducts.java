package com.modules.application.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AgencyProducts {
  private String rateSel;
  private String name;
  private String price;
  private String offer;
  private String month;
  private String feePerCase;
  private String excessPerCase;
}
