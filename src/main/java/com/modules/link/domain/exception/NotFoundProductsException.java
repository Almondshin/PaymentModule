package com.modules.link.domain.exception;

import com.modules.link.enums.EnumResultCode;
import lombok.Getter;

@Getter
public class NotFoundProductsException extends RuntimeException{
    private final EnumResultCode enumResultCode;
    public NotFoundProductsException(EnumResultCode enumResultCode) {
        this.enumResultCode = enumResultCode;
    }
}