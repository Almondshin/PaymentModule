package com.modules.link.persistence.agency;

import com.modules.link.domain.agency.Site;
import com.modules.link.domain.agency.SiteId;
import org.springframework.data.jpa.repository.JpaRepository;


interface SiteJpaRepository extends JpaRepository<Site, SiteId> {
}
