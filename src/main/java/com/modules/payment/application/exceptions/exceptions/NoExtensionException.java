package com.modules.payment.application.exceptions.exceptions;

import com.modules.payment.application.exceptions.enums.EnumResultCode;
import lombok.Getter;

@Getter
public class NoExtensionException extends RuntimeException{
    private final EnumResultCode enumResultCode;
    private final String siteId;
    public NoExtensionException(EnumResultCode enumResultCode, String siteId) {
        this.enumResultCode = enumResultCode;
        this.siteId = siteId;
    }

}
