package com.modules.link.persistence.agency;

import com.modules.link.domain.agency.Agency;
import com.modules.link.domain.agency.SiteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;

interface AgencyJpaRepository extends JpaRepository<Agency, SiteId> {
    Agency findBySiteId(SiteId siteId);
}
