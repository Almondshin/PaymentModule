package com.modules.payment.adapter.out.persistence.repository;

import com.modules.payment.adapter.out.persistence.entity.AgencyInfoKeyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgencyInfoKeyRepository extends JpaRepository<AgencyInfoKeyJpaEntity, String> {
    Optional<AgencyInfoKeyJpaEntity> findByAgencyId(String agencyId);
}
