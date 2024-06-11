package com.modules.link.domain.agency;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.modules.base.domain.StringTypeIdentifier;
import com.modules.base.jpa.hibernate.StringTypeIdentifierJavaType;
import com.modules.link.controller.exception.IllegalAgencyIdSiteIdException;
import com.modules.link.controller.exception.NullAgencyIdSiteIdException;
import com.modules.link.enums.EnumResultCode;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

@JsonSerialize(using = SiteIdSerializer.class)
public class SiteId extends StringTypeIdentifier implements Serializable {
    public static SiteId of(String id) {
        return new SiteId(id);
    }

    public SiteId(String id) {
        super(id);
        validate(this);
    }

    @Override
    public String toString() {
        return stringValue();
    }

    public static void validate(SiteId siteId){
        if (Objects.isNull(siteId)){
            throw new NullAgencyIdSiteIdException(EnumResultCode.NullPointArgument, null);
        }
        if (containsSpecialCharacters(siteId.toString()) || siteId.stringValue().length() > 10){
            throw new IllegalAgencyIdSiteIdException(EnumResultCode.IllegalArgument, siteId.toString());
        }
    }

    private static boolean containsSpecialCharacters(String value) {
        String specialCharacters = "[^a-zA-Z0-9]";
        Pattern pattern = Pattern.compile(specialCharacters);
        return pattern.matcher(value).find();
    }

    public static class SiteIdJavaType extends StringTypeIdentifierJavaType<SiteId> {
        public SiteIdJavaType() {
            super(SiteId.class);
        }
    }

}