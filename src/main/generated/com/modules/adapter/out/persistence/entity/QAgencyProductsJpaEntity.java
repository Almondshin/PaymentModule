package com.modules.adapter.out.persistence.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAgencyProductsJpaEntity is a Querydsl query type for AgencyProductsJpaEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAgencyProductsJpaEntity extends EntityPathBase<AgencyProductsJpaEntity> {

    private static final long serialVersionUID = -2093720990L;

    public static final QAgencyProductsJpaEntity agencyProductsJpaEntity = new QAgencyProductsJpaEntity("agencyProductsJpaEntity");

    public final StringPath excessPerCase = createString("excessPerCase");

    public final StringPath feePerCase = createString("feePerCase");

    public final StringPath month = createString("month");

    public final StringPath name = createString("name");

    public final StringPath offer = createString("offer");

    public final StringPath price = createString("price");

    public final StringPath rateSel = createString("rateSel");

    public QAgencyProductsJpaEntity(String variable) {
        super(AgencyProductsJpaEntity.class, forVariable(variable));
    }

    public QAgencyProductsJpaEntity(Path<? extends AgencyProductsJpaEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAgencyProductsJpaEntity(PathMetadata metadata) {
        super(AgencyProductsJpaEntity.class, metadata);
    }

}

