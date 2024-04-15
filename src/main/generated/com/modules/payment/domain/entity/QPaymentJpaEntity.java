package com.modules.payment.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPaymentJpaEntity is a Querydsl query type for PaymentJpaEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPaymentJpaEntity extends EntityPathBase<PaymentJpaEntity> {

    private static final long serialVersionUID = 932548885L;

    public static final QPaymentJpaEntity paymentJpaEntity = new QPaymentJpaEntity("paymentJpaEntity");

    public final StringPath agencyId = createString("agencyId");

    public final StringPath amount = createString("amount");

    public final StringPath billKey = createString("billKey");

    public final StringPath billKeyExpireDate = createString("billKeyExpireDate");

    public final DateTimePath<java.util.Date> endDate = createDateTime("endDate", java.util.Date.class);

    public final StringPath extraAmountStatus = createString("extraAmountStatus");

    public final StringPath memo = createString("memo");

    public final DateTimePath<java.util.Date> modDate = createDateTime("modDate", java.util.Date.class);

    public final StringPath offer = createString("offer");

    public final StringPath paymentStatus = createString("paymentStatus");

    public final StringPath paymentType = createString("paymentType");

    public final StringPath pgTradeNum = createString("pgTradeNum");

    public final StringPath rateSel = createString("rateSel");

    public final StringPath rcptName = createString("rcptName");

    public final DateTimePath<java.util.Date> regDate = createDateTime("regDate", java.util.Date.class);

    public final StringPath siteId = createString("siteId");

    public final DateTimePath<java.util.Date> startDate = createDateTime("startDate", java.util.Date.class);

    public final StringPath tradeNum = createString("tradeNum");

    public final DateTimePath<java.util.Date> trDate = createDateTime("trDate", java.util.Date.class);

    public final StringPath trTrace = createString("trTrace");

    public final StringPath useCount = createString("useCount");

    public final StringPath vbankAccount = createString("vbankAccount");

    public final DateTimePath<java.util.Date> vbankExpireDate = createDateTime("vbankExpireDate", java.util.Date.class);

    public final StringPath vbankName = createString("vbankName");

    public QPaymentJpaEntity(String variable) {
        super(PaymentJpaEntity.class, forVariable(variable));
    }

    public QPaymentJpaEntity(Path<? extends PaymentJpaEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPaymentJpaEntity(PathMetadata metadata) {
        super(PaymentJpaEntity.class, metadata);
    }

}

