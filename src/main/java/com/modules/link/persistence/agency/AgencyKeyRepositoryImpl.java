package com.modules.link.persistence.agency;

import com.modules.base.jpa.BaseRepository;
import com.modules.link.domain.agency.AgencyId;
import com.modules.link.domain.agency.AgencyKey;
import com.modules.link.domain.agency.AgencyKeyRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class AgencyKeyRepositoryImpl extends BaseRepository<AgencyKey, AgencyId, AgencyKeyJpaRepository> implements AgencyKeyRepository {
    public AgencyKeyRepositoryImpl(AgencyKeyJpaRepository repository) {
        super(repository);
    }

    @Override
    public List<AgencyKey> findAll() {
        return repository.findAll();
    }
}
