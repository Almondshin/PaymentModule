package com.modules.link.application.service.exception;

import com.modules.link.enums.EnumResultCode;
import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {
    private final EnumResultCode enumResultCode;
    private final String siteId;

    public EntityNotFoundException(EnumResultCode enumResultCode, String siteId) {
        this.enumResultCode = enumResultCode;
        this.siteId = siteId;
    }
}
