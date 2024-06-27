package com.modules.link.domain.agency;

import com.modules.base.domain.Repository;

import java.util.List;

public interface AgencyKeyRepository extends Repository<AgencyKey, AgencyId> {
    List<AgencyKey> findAll();
}
