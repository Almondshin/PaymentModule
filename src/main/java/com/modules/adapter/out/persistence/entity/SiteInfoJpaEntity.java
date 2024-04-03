package com.modules.adapter.out.persistence.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "SITE_INFO")
public class SiteInfoJpaEntity {

    @Id
    @Column(name = "SITE_ID")
    private String siteId;
    @Column(name = "SITE_STATUS")
    private String siteStatus;
}
