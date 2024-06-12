package com.modules.link.service.exception;

import com.modules.link.enums.EnumResultCode;
import lombok.Getter;

@Getter
public class NoSuchFieldException extends RuntimeException{
    private final EnumResultCode enumResultCode;
    private final String fieldName;
    public NoSuchFieldException(EnumResultCode enumResultCode, String fieldName) {
        this.enumResultCode = enumResultCode;
        this.fieldName = fieldName;
    }
}
