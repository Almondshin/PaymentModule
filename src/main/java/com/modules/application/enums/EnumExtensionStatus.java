package com.modules.application.enums;

import lombok.Getter;

@Getter
public enum EnumExtensionStatus {
    DEFAULT("D", "초기신청"),
    EXTENDABLE("Y", "연장가능"),
    NOT_EXTENDABLE("N", "연장불가능");


    private final String code;
    private final String value;

    EnumExtensionStatus(String code, String value) {
        this.code = code;
        this.value = value;
    }

}
