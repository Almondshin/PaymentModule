package com.modules.link.persistence.agency;

import com.modules.base.jpa.BaseRepository;
import com.modules.link.domain.agency.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class AgencyRepositoryImpl extends BaseRepository<Agency, SiteId, AgencyJpaRepository> implements AgencyRepository {

    private final SiteJpaRepository siteRepository;
    private final AgencyKeyJpaRepository agencyKeyRepository;

    public AgencyRepositoryImpl(AgencyJpaRepository agencyRepository, SiteJpaRepository siteRepository, AgencyKeyJpaRepository agencyKeyRepository) {
        super(agencyRepository);
        this.siteRepository = siteRepository;
        this.agencyKeyRepository = agencyKeyRepository;
    }

    @Transactional
    @Override
    public Agency find(SiteId id) {
        Optional<Agency> agency = repository.findById(id);
        return agency.orElse(null);
    }

    @Transactional
    @Override
    public Site findSite(SiteId id) {
        Optional<Site> site = siteRepository.findById(id);
        return site.orElse(null);
    }

    @Transactional
    @Override
    public AgencyKey findAgencyKey(AgencyId id) {
        Optional<AgencyKey> agencyKey = agencyKeyRepository.findById(id);
        return agencyKey.orElse(null);
    }
}
