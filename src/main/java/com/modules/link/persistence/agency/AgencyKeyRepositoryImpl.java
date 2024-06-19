package com.modules.link.persistence.agency;

import com.modules.base.jpa.BaseRepository;
import com.modules.link.domain.agency.AgencyId;
import com.modules.link.domain.agency.AgencyKey;
import com.modules.link.domain.agency.AgencyKeyRepository;
import org.springframework.stereotype.Repository;

@Repository
public class AgencyKeyRepositoryImpl extends BaseRepository<AgencyKey, AgencyId, AgencyKeyJpaRepository> implements AgencyKeyRepository {
    public AgencyKeyRepositoryImpl(AgencyKeyJpaRepository repository) {
        super(repository);
    }
}
