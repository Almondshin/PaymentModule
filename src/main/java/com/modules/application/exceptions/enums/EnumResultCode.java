package com.modules.application.exceptions.enums;

public enum EnumResultCode {
    SUCCESS("2000", "성공"),
    IllegalArgument("2900", "데이터형식이 올바르지 않습니다."),
    NullPointArgument("2980", "제휴사ID 또는 상점ID가 비어있습니다."),
    DuplicateMember("2999", "이미 사용중인 아이디입니다."),
    ReadyProducts("3680", "상품 준비중입니다."),
    Subscription("3700", "정기결제를 이용중인 가맹점입니다."),
    PendingApprovalStatus("3800", "제휴사 승인대기 상태입니다."),
    PendingTelcoApprovalStatus("3810", "통신사 승인대기 상태입니다."),
    NoExtension("3988", "결제 가능 기간이 아닙니다."),
    UnregisteredAgency("3999", "등록되지 않은 가맹점입니다."),
    RejectAgency("3499", "심사 반려된 가맹점입니다."),
    SuspendedSiteId("4999", "이용정지된 사이트 아이디입니다.");

    private final String code;
    private final String value;

    EnumResultCode(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static String fromCode(String code) {
        for (EnumResultCode resultCode : EnumResultCode.values()) {
            if (resultCode.getCode().equals(code)) {
                return resultCode.getCode();
            }
        }
        throw new IllegalArgumentException("Invalid resultCode: " + code);
    }

}
