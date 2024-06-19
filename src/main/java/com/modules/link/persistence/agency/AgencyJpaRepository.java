package com.modules.link.persistence.agency;

import com.modules.link.domain.agency.Agency;
import com.modules.link.domain.agency.SiteId;
import org.springframework.data.jpa.repository.JpaRepository;


interface AgencyJpaRepository extends JpaRepository<Agency, SiteId> {
}
