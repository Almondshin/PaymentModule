package com.modules.application.enums;

import lombok.Getter;

@Getter
public enum EnumTradeTrace {
    NOT_USED("N", "미사용"),
    USED("Y", "사용");

    private final String code;
    private final String value;

    EnumTradeTrace(String code, String value) {
        this.code = code;
        this.value = value;
    }

}
