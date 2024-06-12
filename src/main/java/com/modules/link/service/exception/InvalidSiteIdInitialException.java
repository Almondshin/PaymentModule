package com.modules.link.service.exception;

import com.modules.link.enums.EnumResultCode;
import lombok.Getter;

@Getter
public class InvalidSiteIdInitialException extends RuntimeException {
    private final EnumResultCode enumResultCode;
    private final String agencyId;
    private final String siteId;

    public InvalidSiteIdInitialException(EnumResultCode enumResultCode, String agencyId, String siteId) {
        this.enumResultCode = enumResultCode;
        this.agencyId = agencyId;
        this.siteId = siteId;
    }
}
