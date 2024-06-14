package com.modules.link.enums;

import lombok.Getter;

@Getter
public enum EnumBillingBase {
    INCOMPLETE("incomplete", "요청 성공"),
    SUCCESS_FINAL("successFinal" , "최종 성공");

    private final String code;
    private final String value;
    EnumBillingBase(String code, String value) {
        this.code = code;
        this.value = value;
    }

}
