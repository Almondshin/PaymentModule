package com.modules.adapter.out.persistence.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAgencyInfoKeyJpaEntity is a Querydsl query type for AgencyInfoKeyJpaEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAgencyInfoKeyJpaEntity extends EntityPathBase<AgencyInfoKeyJpaEntity> {

    private static final long serialVersionUID = 893928197L;

    public static final QAgencyInfoKeyJpaEntity agencyInfoKeyJpaEntity = new QAgencyInfoKeyJpaEntity("agencyInfoKeyJpaEntity");

    public final StringPath agencyId = createString("agencyId");

    public final StringPath agencyIv = createString("agencyIv");

    public final StringPath agencyKey = createString("agencyKey");

    public final StringPath agencyProductType = createString("agencyProductType");

    public final StringPath agencyUrl = createString("agencyUrl");

    public final StringPath billingBase = createString("billingBase");

    public final DateTimePath<java.util.Date> modDate = createDateTime("modDate", java.util.Date.class);

    public final DateTimePath<java.util.Date> regDate = createDateTime("regDate", java.util.Date.class);

    public final StringPath siteName = createString("siteName");

    public QAgencyInfoKeyJpaEntity(String variable) {
        super(AgencyInfoKeyJpaEntity.class, forVariable(variable));
    }

    public QAgencyInfoKeyJpaEntity(Path<? extends AgencyInfoKeyJpaEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAgencyInfoKeyJpaEntity(PathMetadata metadata) {
        super(AgencyInfoKeyJpaEntity.class, metadata);
    }

}

