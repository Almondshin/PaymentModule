package com.modules.link.domain.agency;

import com.modules.base.domain.DomainEntity;
import lombok.Getter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import java.util.Objects;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@Table(name = "SITE_INFO")
public class Site extends DomainEntity<Site, SiteId> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Type(type = "com.modules.link.domain.agency.SiteId$SiteIdJavaType")
    @Column(name = "SITE_ID")
    private SiteId id;

    @Column(name = "SITE_STATUS")
    private String siteStatus;

    public boolean isAvailable(){
        return !Objects.equals(siteStatus, "N");
    }


}
