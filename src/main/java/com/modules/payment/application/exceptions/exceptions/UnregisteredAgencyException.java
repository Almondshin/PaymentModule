package com.modules.payment.application.exceptions.exceptions;
import com.modules.payment.application.exceptions.enums.EnumResultCode;
import lombok.Getter;

@Getter
public class UnregisteredAgencyException extends RuntimeException {
    private final EnumResultCode enumResultCode;
    public UnregisteredAgencyException(EnumResultCode enumResultCode) {
        this.enumResultCode = enumResultCode;
    }
}
