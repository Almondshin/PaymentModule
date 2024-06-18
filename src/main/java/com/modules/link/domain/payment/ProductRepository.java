package com.modules.link.domain.payment;


import com.modules.base.domain.Repository;
import com.modules.link.domain.agency.AgencyId;
import com.modules.link.domain.agency.SiteId;

import java.util.List;

public interface ProductRepository extends Repository<Product, RateSel> {
    List<Product> findByAgencyId(AgencyId agencyId);

}
