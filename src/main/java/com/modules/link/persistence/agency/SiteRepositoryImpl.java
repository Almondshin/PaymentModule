package com.modules.link.persistence.agency;

import com.modules.base.jpa.BaseRepository;
import com.modules.link.domain.agency.Site;
import com.modules.link.domain.agency.SiteId;
import com.modules.link.domain.agency.SiteRepository;
import org.springframework.stereotype.Repository;

@Repository
class SiteRepositoryImpl extends BaseRepository<Site, SiteId, SiteJpaRepository> implements SiteRepository {
    public SiteRepositoryImpl(SiteJpaRepository siteJpaRepository) {
        super(siteJpaRepository);
    }
}
