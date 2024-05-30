package com.modules.link.domain.agency;

import com.modules.base.domain.AggregateRoot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modules.link.enums.EnumExtensionStatus;
import com.modules.link.enums.EnumSiteStatus;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@ToString
@Table(name = "AGENCY_INFO")
public class Agency extends AggregateRoot<Agency, SiteId> {

    public static final String SITE_INFO = "info";
    public static final String STATUS_TYPE = "status";
    private static final String REGISTER_TYPE = "reg";
    private static final String CANCEL_TYPE = "cancel";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Type(type = "com.modules.link.domain.agency.SiteId$SiteIdJavaType")
    @Column(name = "SITE_ID")
    private SiteId id;

    @Type(type = "com.modules.link.domain.agency.AgencyId$AgencyIdJavaType")
    @Column(name = "AGENCY_ID")
    private AgencyId agencyId;

    @Column(name = "SITE_STATUS")
    private String agencyStatus;

    @Column(name = "EXTENSION_STATUS")
    private String extensionStatus;

    @Embedded
    private AgencyCompany agencyCompany;

    @Embedded
    private AgencyPayment agencyPayment;

    @Embedded
    private AgencyManager agencyManager;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SITE_ID", insertable = false, updatable = false)
    private Site site;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AGENCY_ID", insertable = false, updatable = false)
    private AgencyKey agencyKey;



    private void addSite(Agency agency, Site site) {
        if(agency != null) {
            throw new IllegalArgumentException("Agency already exists");
        }

        if (site != null){
            throw new IllegalArgumentException("Site already exists");
        }

        if (!site.isAvailable()){
            throw new IllegalArgumentException("사이트는 사용중이여야 합니다.");
        }

    }

    public String makeVerifyAndEncryptData(String type) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new HashMap<>();
        try {
            switch (type) {
                case STATUS_TYPE: {
                    map.put("siteId", this.id.toString());
                    map.put("siteStatus", this.agencyStatus);
                    System.out.println("SITE STATUS MAP " + map);
                    return mapper.writeValueAsString(map);
                }
                case REGISTER_TYPE: {
                    return mapper.writeValueAsString(this);
                }
                case CANCEL_TYPE: {
                    map.put("agencyId", this.agencyId.toString());
                    map.put("siteId", this.id.toString());
                    map.put("siteName", this.agencyCompany.getSiteName());
                    return mapper.writeValueAsString(map);
                }
            }
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Agency of(SiteId siteId, AgencyId agencyId, AgencyCompany agencyCompany, AgencyManager agencyManager) {
        Agency agency = new Agency();
        // 여기에서 AgencyCompany, AgencyPayment, Manager를 설정
        agency.id = siteId;
        agency.agencyId = agencyId;
        agency.agencyStatus = EnumSiteStatus.PENDING.getCode();
        agency.extensionStatus = EnumExtensionStatus.DEFAULT.getCode();
        agency.agencyCompany = agencyCompany;
        agency.agencyManager = agencyManager;

        return agency;
    }

}
