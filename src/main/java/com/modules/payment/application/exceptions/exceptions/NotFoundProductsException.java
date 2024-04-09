package com.modules.payment.application.exceptions.exceptions;

import com.modules.payment.application.exceptions.enums.EnumResultCode;
import lombok.Getter;

@Getter
public class NotFoundProductsException extends RuntimeException{
    private final EnumResultCode enumResultCode;
    public NotFoundProductsException(EnumResultCode enumResultCode) {
        this.enumResultCode = enumResultCode;
    }

}
