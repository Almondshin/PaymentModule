package com.modules.link.domain.payment;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.modules.base.domain.StringTypeIdentifier;
import com.modules.base.jpa.hibernate.StringTypeIdentifierJavaType;

import java.io.Serializable;

@JsonSerialize(using = RateSelSerializer.class)
public class RateSel extends StringTypeIdentifier implements Serializable {
    public static RateSel of(String id) {
        return new RateSel(id);
    }

    public RateSel(String id) {
        super(id);
    }

    @Override
    public String toString() {
        return stringValue();
    }

    public static class RateSelJavaType extends StringTypeIdentifierJavaType<RateSel> {
        public RateSelJavaType() {
            super(RateSel.class);
        }
    }
}
