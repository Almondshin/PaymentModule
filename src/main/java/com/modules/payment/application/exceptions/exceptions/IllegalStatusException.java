package com.modules.payment.application.exceptions.exceptions;
import com.modules.payment.application.exceptions.enums.EnumResultCode;
import lombok.Getter;

@Getter
public class IllegalStatusException extends RuntimeException {
    private final EnumResultCode enumResultCode;
    public IllegalStatusException(EnumResultCode enumResultCode) {
        this.enumResultCode = enumResultCode;
    }
}
