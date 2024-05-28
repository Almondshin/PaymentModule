package com.modules.link.domain.agency;

import com.modules.base.domain.StringTypeIdentifier;
import com.modules.base.jpa.hibernate.StringTypeIdentifierJavaType;

import java.io.Serializable;

public class SiteId extends StringTypeIdentifier implements Serializable {
    public static SiteId of(String id) {
        return new SiteId(id);
    }

    public SiteId(String id) {
        super(id);
    }

    @Override
    public String toString() {
        return stringValue();
    }

    public static class SiteIdJavaType extends StringTypeIdentifierJavaType<SiteId> {
        public SiteIdJavaType() {
            super(SiteId.class);
        }
    }

}