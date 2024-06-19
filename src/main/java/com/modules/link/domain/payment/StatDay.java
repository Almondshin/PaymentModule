package com.modules.link.domain.payment;

import com.modules.base.domain.AggregateRoot;
import com.modules.link.domain.agency.SiteId;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@ToString
@Table(name = "STAT_DAY")
@IdClass(StatDayCompositeId.class)
public class StatDay extends AggregateRoot<StatDay, SiteId> {

    @Id
    @Getter
    @Type(type = "com.modules.link.domain.agency.SiteId$SiteIdJavaType")
    @Column(name = "SITE_ID", nullable = false)
    private SiteId id;

    @Id
    @Getter
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
