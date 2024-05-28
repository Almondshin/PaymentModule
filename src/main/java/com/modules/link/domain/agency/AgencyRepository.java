package com.modules.link.domain.agency;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AgencyRepository extends JpaRepository<Agency, SiteId> {

}
