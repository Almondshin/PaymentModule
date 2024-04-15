package com.modules.payment.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAgencyJpaEntity is a Querydsl query type for AgencyJpaEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAgencyJpaEntity extends EntityPathBase<AgencyJpaEntity> {

    private static final long serialVersionUID = 147104924L;

    public static final QAgencyJpaEntity agencyJpaEntity = new QAgencyJpaEntity("agencyJpaEntity");

    public final StringPath address = createString("address");

    public final StringPath agencyId = createString("agencyId");

    public final StringPath bizNumber = createString("bizNumber");

    public final StringPath businessType = createString("businessType");

    public final StringPath ceoName = createString("ceoName");

    public final StringPath companyName = createString("companyName");

    public final StringPath companySite = createString("companySite");

    public final StringPath email = createString("email");

    public final DateTimePath<java.util.Date> endDate = createDateTime("endDate", java.util.Date.class);

    public final StringPath excessCount = createString("excessCount");

    public final StringPath extensionStatus = createString("extensionStatus");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath rateSel = createString("rateSel");

    public final StringPath scheduledRateSel = createString("scheduledRateSel");

    public final StringPath serviceUseAgree = createString("serviceUseAgree");

    public final StringPath settleManagerEmail = createString("settleManagerEmail");

    public final StringPath settleManagerName = createString("settleManagerName");

    public final StringPath settleManagerPhoneNumber = createString("settleManagerPhoneNumber");

    public final StringPath settleManagerTelNumber = createString("settleManagerTelNumber");

    public final StringPath siteId = createString("siteId");

    public final StringPath siteName = createString("siteName");

    public final StringPath siteStatus = createString("siteStatus");

    public final DateTimePath<java.util.Date> startDate = createDateTime("startDate", java.util.Date.class);

    public QAgencyJpaEntity(String variable) {
        super(AgencyJpaEntity.class, forVariable(variable));
    }

    public QAgencyJpaEntity(Path<? extends AgencyJpaEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAgencyJpaEntity(PathMetadata metadata) {
        super(AgencyJpaEntity.class, metadata);
    }

}

