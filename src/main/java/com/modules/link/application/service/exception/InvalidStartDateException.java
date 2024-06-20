package com.modules.link.application.service.exception;

import com.modules.link.enums.EnumResultCode;
import lombok.Getter;

@Getter
public class InvalidStartDateException extends RuntimeException {
    private final EnumResultCode enumResultCode;

    public InvalidStartDateException(EnumResultCode enumResultCode) {
        this.enumResultCode = enumResultCode;
    }
}
