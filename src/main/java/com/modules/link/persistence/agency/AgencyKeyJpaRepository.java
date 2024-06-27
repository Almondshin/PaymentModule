package com.modules.link.persistence.agency;

import com.modules.link.domain.agency.AgencyKey;
import com.modules.link.domain.agency.AgencyId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgencyKeyJpaRepository extends JpaRepository<AgencyKey, AgencyId> {
}
