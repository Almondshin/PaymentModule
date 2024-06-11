package com.modules.link.enums;

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
    SuspendedSiteId("4999", "이용정지된 사이트 아이디입니다."),
    HmacError("9100", "HMAC 검증에 실패하였습니다."),
    MsgTypeError("9200", "MsgType 검증이 실패하였습니다."),
    InvalidSiteIdInitial("9999", "제휴사 siteId Initial 검증 실패"),
    NoSuchFieldError("9999", " 필드가 비어 있습니다.");

    private final String code;
    private final String message;

    EnumResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
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
