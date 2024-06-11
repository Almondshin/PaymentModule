package com.modules.link.controller.exception;
import com.modules.link.enums.EnumResultCode;
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
