package com.modules.application.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatDay {
  private String fromDate;
  private String mokClientId;
  private String siteId;
  private String providerId;
  private String serviceType;
  private long reqCnt;
  private long successFinalCnt;
  private java.sql.Timestamp regDate;
  private long incompleteCnt;
}
