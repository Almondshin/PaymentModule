package com.modules.link.domain.agency;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.modules.base.domain.StringTypeIdentifier;
import com.modules.base.jpa.hibernate.StringTypeIdentifierJavaType;

import java.io.Serializable;

@JsonSerialize(using = SiteIdSerializer.class)
public class AgencyId extends StringTypeIdentifier implements Serializable {
    public static AgencyId of(String id) {
        return new AgencyId(id);
    }

    public AgencyId(String id) {
        super(id);
    }

    @Override
    public String toString() {
        return stringValue();
    }

    public static class AgencyIdJavaType extends StringTypeIdentifierJavaType<AgencyId> {
        public AgencyIdJavaType() {
            super(AgencyId.class);
        }
    }

}