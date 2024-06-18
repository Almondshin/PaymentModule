package com.modules.link.persistence.payment;

import com.modules.base.jpa.BaseRepository;
import com.modules.link.domain.agency.AgencyId;
import com.modules.link.domain.agency.SiteId;
import com.modules.link.domain.payment.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
class ProductRepositoryImpl extends BaseRepository<Product, RateSel, ProductJpaRepository> implements ProductRepository {
    public ProductRepositoryImpl(ProductJpaRepository repository) {
        super(repository);
    }

    @Transactional
    @Override
    public List<Product> findByAgencyId(AgencyId agencyId) {
        return repository.findByAgencyId(agencyId);
    }
    

}
