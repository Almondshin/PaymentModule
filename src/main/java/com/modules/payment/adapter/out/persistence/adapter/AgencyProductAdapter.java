package com.modules.payment.adapter.out.persistence.adapter;

import com.modules.payment.domain.entity.AgencyProductsJpaEntity;
import com.modules.payment.adapter.out.persistence.repository.AgencyProductRepository;
import com.modules.payment.application.domain.AgencyProducts;
import com.modules.payment.application.port.out.load.LoadAgencyProductDataPort;
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
    public AgencyProducts getAgencyProductByRateSel(String rateSel) {
        Optional<AgencyProductsJpaEntity> entity = agencyProductRepository.findByRateSel(rateSel);
        return entity.map(this::convertDomain).orElse(null);
    }

    private AgencyProducts convertDomain(AgencyProductsJpaEntity entity) {
        return AgencyProducts.builder()
                .rateSel(entity.getRateSel())
                .name(entity.getName())
                .price(entity.getPrice())
                .offer(entity.getOffer())
                .month(entity.getMonth())
                .feePerCase(entity.getFeePerCase())
                .excessPerCase(entity.getExcessPerCase())
                .build();
    }
}
