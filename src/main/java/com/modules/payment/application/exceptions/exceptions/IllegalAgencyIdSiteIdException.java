package com.modules.payment.application.exceptions.exceptions;
import com.modules.payment.application.exceptions.enums.EnumResultCode;
import lombok.Getter;

@Getter
public class IllegalAgencyIdSiteIdException extends RuntimeException {
    private final EnumResultCode enumResultCode;
    private final String siteId;
    public IllegalAgencyIdSiteIdException(EnumResultCode enumResultCode, String siteId) {
        this.enumResultCode = enumResultCode;
        this.siteId = siteId;
    }


}