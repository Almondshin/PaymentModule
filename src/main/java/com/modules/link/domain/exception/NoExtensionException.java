package com.modules.link.domain.exception;

import com.modules.link.enums.EnumResultCode;
import lombok.Getter;

@Getter
public class NoExtensionException extends RuntimeException{
    private final EnumResultCode enumResultCode;
    public NoExtensionException(EnumResultCode enumResultCode) {
        this.enumResultCode = enumResultCode;
    }

}
