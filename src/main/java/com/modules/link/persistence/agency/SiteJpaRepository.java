package com.modules.link.persistence.agency;

import com.modules.link.domain.agency.Site;
import com.modules.link.domain.agency.SiteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;

public interface SiteJpaRepository extends JpaRepository<Site, SiteId> {
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Site findBySiteId(SiteId siteId);
}
