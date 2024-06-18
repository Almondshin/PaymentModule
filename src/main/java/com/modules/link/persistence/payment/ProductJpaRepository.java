package com.modules.link.persistence.payment;

import com.modules.link.domain.agency.AgencyId;
import com.modules.link.domain.agency.SiteId;
import com.modules.link.domain.payment.Product;
import com.modules.link.domain.payment.RateSel;
import com.modules.link.domain.payment.StatDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductJpaRepository extends JpaRepository<Product, RateSel> {
    List<Product> findByAgencyId(AgencyId agencyId);
}
