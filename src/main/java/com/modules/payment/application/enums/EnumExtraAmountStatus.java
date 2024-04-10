package com.modules.payment.application.enums;

import lombok.Getter;

@Getter
public enum EnumExtraAmountStatus {
    PASS("P", "통과"),
    SYSTEM_COMPLETE("S", "시스템 초과금 정산 완료"),
    MANAGER_COMPLETE("C", "관리자 초과금 정산 완료");

    private final String code;
    private final String value;

    EnumExtraAmountStatus(String code, String value) {
        this.code = code;
        this.value = value;
    }

}
