package com.modules.payment.adapter.out.persistence.adapter;

import com.modules.payment.adapter.out.persistence.repository.AgencyProductRepository;
import com.modules.payment.application.mapper.ProductMapper;
import com.modules.payment.application.port.out.load.LoadAgencyProductDataPort;
import com.modules.payment.domain.Product;
import com.modules.payment.domain.entity.AgencyProductsJpaEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AgencyProductAdapter implements LoadAgencyProductDataPort {

    private final AgencyProductRepository agencyProductRepository;

    public AgencyProductAdapter(AgencyProductRepository agencyProductRepository) {
        this.agencyProductRepository = agencyProductRepository;
    }

    @Override
    @Transactional
    public Optional<Product> getAgencyProductByRateSel(String rateSel) {
        Optional<AgencyProductsJpaEntity> entity = agencyProductRepository.findByRateSel(rateSel);
        return entity.map(ProductMapper::convertToDomain);
    }


}
