package com.modules.payment.application.enums;

import lombok.Getter;

@Getter
public enum EnumAgree {
    DISAGREE("N", "이용약관미동의"),
    AGREE("Y", "이용약관동의");

    private final String code;
    private final String value;
    EnumAgree(String code, String value) {
        this.code = code;
        this.value = value;
    }

}
