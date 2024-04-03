package com.modules.adapter.out.persistence.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStatDayJpaEntity is a Querydsl query type for StatDayJpaEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStatDayJpaEntity extends EntityPathBase<StatDayJpaEntity> {

    private static final long serialVersionUID = 1471651625L;

    public static final QStatDayJpaEntity statDayJpaEntity = new QStatDayJpaEntity("statDayJpaEntity");

    public final NumberPath<Long> failCnt = createNumber("failCnt", Long.class);

    public final StringPath fromDate = createString("fromDate");

    public final NumberPath<Long> incompleteCnt = createNumber("incompleteCnt", Long.class);

    public final StringPath mokClientId = createString("mokClientId");

    public final StringPath providerId = createString("providerId");

    public final DateTimePath<java.sql.Timestamp> regDate = createDateTime("regDate", java.sql.Timestamp.class);

    public final NumberPath<Long> reqCnt = createNumber("reqCnt", Long.class);

    public final StringPath serviceType = createString("serviceType");

    public final StringPath siteId = createString("siteId");

    public final NumberPath<Long> successCnt = createNumber("successCnt", Long.class);

    public final NumberPath<Long> successFinalCnt = createNumber("successFinalCnt", Long.class);

    public QStatDayJpaEntity(String variable) {
        super(StatDayJpaEntity.class, forVariable(variable));
    }

    public QStatDayJpaEntity(Path<? extends StatDayJpaEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStatDayJpaEntity(PathMetadata metadata) {
        super(StatDayJpaEntity.class, metadata);
    }

}

