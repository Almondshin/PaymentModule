package com.modules.payment.application.mapper;

import com.modules.payment.domain.Product;
import com.modules.payment.domain.entity.AgencyProductsJpaEntity;

public class ProductMapper {

    public static Product convertToDomain(AgencyProductsJpaEntity entity){
        return Product.builder()
                .productCode(entity.getRateSel())
                .productName(entity.getName())
                .price(entity.getPrice())
                .offer(entity.getOffer())
                .month(entity.getMonth())
                .feePerCase(entity.getFeePerCase())
                .excessPerCase(entity.getExcessPerCase())
                .build();
    }
}
