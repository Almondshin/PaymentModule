package com.modules.application.enums;

import lombok.Getter;

@Getter
public enum EnumPaymentStatus {
    INACTIVE("N", "미사용"),
    ACTIVE("Y", "사용중"),
    NOT_DEPOSITED("M", "가상계좌 미입금");


    private final String code;
    private final String value;

    EnumPaymentStatus(String code, String value) {
        this.code = code;
        this.value = value;
    }

}
