package com.modules.link.persistence.agency;

import com.modules.base.jpa.BaseRepository;
import com.modules.link.domain.agency.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
class AgencyRepositoryImpl extends BaseRepository<Agency, SiteId, AgencyJpaRepository> implements AgencyRepository {
    public AgencyRepositoryImpl(AgencyJpaRepository agencyRepository) {
        super(agencyRepository);
    }
}
