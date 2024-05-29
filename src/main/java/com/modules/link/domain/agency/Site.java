package com.modules.link.domain.agency;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
public class Site {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "SITE_STATUS")
    private char siteStatus;


    public boolean isAvailable(){
        return siteStatus != 'N';
    }


}
