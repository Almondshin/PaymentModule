package com.modules.link.domain.agency;

import com.modules.base.domain.DomainEntity;
import lombok.Getter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;


@Entity
@Table(name = "AGENCY_INFO_KEY")
public class AgencyKey extends DomainEntity<AgencyKey, AgencyId> {

    @Getter
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Type(type = "com.modules.link.domain.agency.AgencyId$AgencyIdJavaType")
    @Column(name = "AGENCY_ID")
    private AgencyId id;

    @Column(name = "AGENCY_KEY")
    private String agencyKey;
    @Column(name = "AGENCY_IV")
    private String agencyIv;

    @Column(name = "AGENCY_URL")
    private String agencyUrl;
    @Column(name = "AGENCY_PRODUCT_TYPE")
    private String productList;
    @Getter
    @Column(name = "BILLING_BASE")
    private String billingBase;

    public String keyString() {
        return this.id.toString();
    }

    public String getKey() {
        return this.agencyKey;
    }

    public String getIv() {
        return this.agencyIv;
    }

}