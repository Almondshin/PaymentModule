package com.modules.link.domain.agency;

import com.modules.base.domain.AggregateRoot;
import com.modules.link.enums.EnumExtensionStatus;
import com.modules.link.enums.EnumSiteStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Type;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Entity
@Getter
@Slf4j
@Validated
@NoArgsConstructor
@Table(name = "AGENCY_INFO")
public class Agency extends AggregateRoot<Agency, SiteId> {

    public static final String SITE_INFO = "info";
    public static final String STATUS_TYPE = "status";
    private static final String REGISTER_TYPE = "reg";
    private static final String CANCEL_TYPE = "cancel";

    @Id
    @Type(type = "com.modules.link.domain.agency.SiteId$SiteIdJavaType")
    @Column(name = "SITE_ID")
    @NotNull(message = "siteId")
    private SiteId id;

    @Type(type = "com.modules.link.domain.agency.AgencyId$AgencyIdJavaType")
    @Column(name = "AGENCY_ID", nullable = false)
    @NotNull(message = "agencyId")
    private AgencyId agencyId;

    @Column(name = "SITE_STATUS")
    private String agencyStatus;

    @Column(name = "EXTENSION_STATUS")
    private String extensionStatus;

    @Column(name = "REG_DATE")
    private LocalDateTime registeredTime;

    /*
     * 객체 그래프를 탐색하며 제약조건을 적용하기 때문에
     * Valid 어노테이션을 사용하지 않더라도 객체 검증이 이루어 지지만
     * 명시적으로 해당 객체도 검증한다고 표현하기 위해서 붙임
     * */
    @Embedded
    @Valid
    private AgencyCompany agencyCompany;

    @Embedded
    private AgencyPayment agencyPayment;

    @Embedded
    @Valid
    private AgencyManager agencyManager;

//    //서로 같은 PK를 사용하여 호출을 하는경우 FetchType의 LAZY옵션이 적용되지 않음
//    // 해결방안 > optional = false로 지정
//    @OneToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "SITE_ID", insertable = false, updatable = false)
//    private Site site;

    public Agency addSite() {
        return new Agency(this.id, this.agencyId, this.getAgencyCompany(), this.getAgencyManager());
    }

    private Agency(SiteId siteId, AgencyId agencyId, AgencyCompany agencyCompany, AgencyManager agencyManager) {
        this(siteId, agencyId, EnumSiteStatus.PENDING.getCode(), EnumExtensionStatus.DEFAULT.getCode(), LocalDateTime.now(), agencyCompany, agencyManager);
    }

    public static Agency of(SiteId siteId, AgencyId agencyId, AgencyCompany agencyCompany, AgencyManager agencyManager) {
        if (siteId == null) {
            throw new IllegalArgumentException("제휴사의 siteId는 Null이 아니어야 합니다.");
        }
        if (agencyId == null) {
            throw new IllegalArgumentException("제휴사의 ID는 Null이 아니어야 합니다.");
        }
        return Agency.builder()
                .id(siteId)
                .agencyId(agencyId)
                .agencyCompany(agencyCompany)
                .agencyManager(agencyManager)
                .build();
    }

    @Builder
    private Agency(SiteId id, AgencyId agencyId, AgencyCompany agencyCompany, AgencyPayment agencyPayment, AgencyManager agencyManager) {
        this.id = id;
        this.agencyId = agencyId;
        this.agencyCompany = agencyCompany;
        this.agencyPayment = agencyPayment;
        this.agencyManager = agencyManager;
    }

    @Builder
    public Agency(SiteId id, AgencyId agencyId, String siteStatus, String extensionStatus, LocalDateTime registeredTime, AgencyCompany agencyCompany, AgencyManager agencyManager) {
        this.id = id;
        this.agencyId = agencyId;
        this.agencyStatus = siteStatus;
        this.registeredTime = registeredTime;
        this.extensionStatus = extensionStatus;
        this.agencyCompany = agencyCompany;
        this.agencyManager = agencyManager;
    }


}
