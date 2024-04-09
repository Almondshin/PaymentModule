package com.modules.payment.adapter.out.persistence.repository;

import com.modules.payment.adapter.out.persistence.entity.AgencyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgencyRepository extends JpaRepository<AgencyJpaEntity, String> {
    Optional<AgencyJpaEntity> findByAgencyIdAndSiteId(String agencyId, String siteId);
    Optional<AgencyJpaEntity> findBySiteId(String siteId);
}
