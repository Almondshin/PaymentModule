package com.modules.payment.domain.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "STAT_DAY")
public class StatDayJpaEntity {

  @Id
  @Column(name = "FROM_DATE")
  private String fromDate;
  @Column(name = "MOK_CLIENT_ID")
  private String mokClientId;
  @Column(name = "SITE_ID")
  private String siteId;
  @Column(name = "PROVIDER_ID")
  private String providerId;
  @Column(name = "SERVICE_TYPE")
  private String serviceType;

  @Column(name = "REQ_CNT")
  private long reqCnt;

  @Column(name = "SUCCESS_CNT")
  private long successCnt;

  @Column(name = "SUCCESS_FINAL_CNT")
  private long successFinalCnt;

  @Column(name = "FAIL_CNT")
  private long failCnt;

  @Column(name = "REG_DATE")
  private java.sql.Timestamp regDate;

  @Column(name = "INCOMPLETE_CNT")
  private long incompleteCnt;

}
