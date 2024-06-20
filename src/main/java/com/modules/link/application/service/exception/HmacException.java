package com.modules.link.application.service.exception;

import com.modules.link.enums.EnumResultCode;
import lombok.Getter;

@Getter
public class HmacException extends RuntimeException {
    private final EnumResultCode enumResultCode;

    public HmacException(EnumResultCode enumResultCode) {
        this.enumResultCode = enumResultCode;
    }
}
