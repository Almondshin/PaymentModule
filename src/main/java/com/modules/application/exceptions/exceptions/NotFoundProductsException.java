package com.modules.application.exceptions.exceptions;

import com.modules.application.exceptions.enums.EnumResultCode;
import lombok.Getter;

@Getter
public class NotFoundProductsException extends RuntimeException{
    private final EnumResultCode enumResultCode;
    public NotFoundProductsException(EnumResultCode enumResultCode) {
        this.enumResultCode = enumResultCode;
    }

}
