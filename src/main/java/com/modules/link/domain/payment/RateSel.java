package com.modules.link.domain.payment;

import com.modules.base.domain.StringTypeIdentifier;
import com.modules.base.jpa.hibernate.StringTypeIdentifierJavaType;

public class RateSel extends StringTypeIdentifier {
    public static RateSel of(String id) {
        return new RateSel(id);
    }

    public RateSel(String id) {
        super(id);
    }

    public static class RateSelJavaType extends StringTypeIdentifierJavaType<RateSel> {
        public RateSelJavaType() {
            super(RateSel.class);
        }
    }
}
