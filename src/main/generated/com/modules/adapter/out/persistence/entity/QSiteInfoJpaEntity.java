package com.modules.adapter.out.persistence.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSiteInfoJpaEntity is a Querydsl query type for SiteInfoJpaEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSiteInfoJpaEntity extends EntityPathBase<SiteInfoJpaEntity> {

    private static final long serialVersionUID = 1114350998L;

    public static final QSiteInfoJpaEntity siteInfoJpaEntity = new QSiteInfoJpaEntity("siteInfoJpaEntity");

    public final StringPath siteId = createString("siteId");

    public final StringPath siteStatus = createString("siteStatus");

    public QSiteInfoJpaEntity(String variable) {
        super(SiteInfoJpaEntity.class, forVariable(variable));
    }

    public QSiteInfoJpaEntity(Path<? extends SiteInfoJpaEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSiteInfoJpaEntity(PathMetadata metadata) {
        super(SiteInfoJpaEntity.class, metadata);
    }

}

