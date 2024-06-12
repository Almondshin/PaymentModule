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
@JsonSerialize(using = AgencyIdSerializer.class)
public class AgencyId extends StringTypeIdentifier implements Serializable {
    public static AgencyId of(String id) {
        return new AgencyId(id);
    }

    public AgencyId(String id) {
        super(id);
        validate(this);
    }

    @Override
    public String toString() {
        return stringValue();
    }


    public static void validate(AgencyId agencyId) {
        if (StringUtils.isBlank(agencyId.toString())){
            log.error("AgencyId가 비어있습니다.: {}", agencyId.stringValue());
            throw new NullAgencyIdSiteIdException(EnumResultCode.NullPointArgument, null);
        }
        if (containsSpecialCharacters(agencyId.toString())){
            log.error("AgencyId에 특수문자가 존재합니다.: {}", agencyId.stringValue());
            throw new IllegalAgencyIdSiteIdException(EnumResultCode.IllegalArgument, agencyId.toString());
        }
    }

    private static boolean containsSpecialCharacters(String value) {
        String specialCharacters = "[^a-zA-Z0-9]";
        Pattern pattern = Pattern.compile(specialCharacters);
        return pattern.matcher(value).find();
    }


    public static class AgencyIdJavaType extends StringTypeIdentifierJavaType<AgencyId> {
        public AgencyIdJavaType() {
            super(AgencyId.class);
        }
    }

}