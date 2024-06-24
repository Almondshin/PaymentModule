package com.modules.link.application.service.exception;

import com.modules.link.enums.EnumResultCode;
import lombok.Getter;

@Getter
public class EntityExistsException extends RuntimeException {
    private final EnumResultCode enumResultCode;
    private final String siteId;

    public EntityExistsException(EnumResultCode enumResultCode, String siteId) {
        this.enumResultCode = enumResultCode;
        this.siteId = siteId;
    }
}
