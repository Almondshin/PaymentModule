package com.modules.link.domain.payment;

import com.modules.base.domain.DomainEntity;
import com.modules.link.domain.agency.SiteId;
import lombok.Getter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "STAT_DAY")
public class StatDay extends DomainEntity<StatDay, SiteId> {

    @Id
    @Getter
    @Type(type = "com.modules.link.domain.agency.SiteId$SiteIdJavaType")
    @Column(name = "SITE_ID", nullable = false)
    private SiteId id;

    @Column(name = "FROM_DATE")
    private String fromDate;
    @Column(name = "PROVIDER_ID")
    private String providerId;
    @Column(name = "SERVICE_TYPE")
    private String serviceType;

    @Getter
    @Column(name = "REQ_CNT")
    private int reqCount;

    @Getter
    @Column(name = "INCOMPLETE_CNT")
    private int incompleteCount;
    @Getter
    @Column(name = "SUCCESS_FINAL_CNT")
    private int successFinalCount;

    @Column(name = "REG_DATE")
    private LocalDateTime regDate;

}
