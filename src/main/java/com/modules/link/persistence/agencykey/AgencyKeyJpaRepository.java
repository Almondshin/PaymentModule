package com.modules.link.persistence.agencykey;

import com.modules.link.domain.agency.AgencyId;
import com.modules.link.domain.agency.AgencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgencyKeyJpaRepository extends JpaRepository<AgencyKey, AgencyId> {
}
