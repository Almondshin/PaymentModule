package com.modules.link.domain.agency;

import com.modules.base.domain.AggregateRoot;
import com.modules.base.domain.DomainEntity;
import lombok.Getter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import java.util.Objects;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Entity
public class Site extends DomainEntity<Site, SiteId> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Type(type = "com.modules.link.domain.site.SiteId$SiteIdJavaType")
    @Column(name = "SITE_ID")
    private String id;

    @Column(name = "SITE_STATUS")
    private String siteStatus;

    public boolean isAvailable(){
        return !Objects.equals(siteStatus, "N");
    }


}
