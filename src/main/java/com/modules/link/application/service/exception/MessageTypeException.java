package com.modules.link.application.service.exception;

import com.modules.link.enums.EnumResultCode;
import lombok.Getter;

@Getter
public class MessageTypeException extends RuntimeException {
    private final EnumResultCode enumResultCode;

    public MessageTypeException(EnumResultCode enumResultCode) {
        this.enumResultCode = enumResultCode;
    }
}
