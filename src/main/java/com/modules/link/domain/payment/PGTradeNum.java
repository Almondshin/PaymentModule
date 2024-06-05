package com.modules.link.domain.payment;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.modules.base.domain.StringTypeIdentifier;
import com.modules.base.jpa.hibernate.StringTypeIdentifierJavaType;

import java.io.Serializable;

@JsonSerialize(using = PGTradeNumSerializer.class)
public class PGTradeNum extends StringTypeIdentifier implements Serializable {
    public static PGTradeNum of(String id) {return new PGTradeNum(id);}

    public PGTradeNum(String id) {super(id);}

    @Override
    public String toString() {
        return stringValue();
    }
    public static class PGTradeNumJavaType extends StringTypeIdentifierJavaType<PGTradeNum> {
        public PGTradeNumJavaType() {
            super(PGTradeNum.class);
        }
    }
}
