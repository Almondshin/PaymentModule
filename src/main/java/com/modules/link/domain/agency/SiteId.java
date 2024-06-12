package com.modules.link.domain.agency;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.modules.base.domain.StringTypeIdentifier;
import com.modules.base.jpa.hibernate.StringTypeIdentifierJavaType;
import com.modules.link.domain.exception.IllegalAgencyIdSiteIdException;
import com.modules.link.domain.exception.NullAgencyIdSiteIdException;
import com.modules.link.enums.EnumResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.regex.Pattern;

@Slf4j
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

    public static void validate(SiteId siteId) {
        if (StringUtils.isBlank(siteId.toString())) {
            log.error("SiteId가 비어있습니다.: {}", siteId.stringValue());
            throw new NullAgencyIdSiteIdException(EnumResultCode.NullPointArgument, null);
        }
        if (containsSpecialCharacters(siteId.toString())) {
            log.error("SiteId에 특수문자가 존재합니다.: {}", siteId.stringValue());
            throw new IllegalAgencyIdSiteIdException(EnumResultCode.IllegalArgument, siteId.toString());
        }
        if (siteId.stringValue().length() > 10) {
            log.error("SiteId의 길이가 10자리를 초과 했습니다.: {}", siteId.stringValue());
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