package com.modules.link.enums;

public enum EnumSiteStatus {
    ACTIVE("Y" , "사이트 사용중"),
    SUSPENDED("N" , "사이트 이용정지"),
    UNREGISTERED("E", "제휴사 미등록"),
    PENDING("H" , "제휴사 승인대기"),
    DUPLICATE("D", "이미 등록된 사이트"),
    REJECT("R" , "통신사 심사 반려된 사이트"),
    TRADE_PENDING("P" , "결제 대기");

    private final String code;
    private final String value;

    EnumSiteStatus(String code, String value) {
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
        for (EnumSiteStatus siteStatus : EnumSiteStatus.values()) {
            if (siteStatus.getCode().equals(code)) {
                return siteStatus.getCode(); // 여기서 EnumResultCode의 code 값을 반환
            }
        }
        throw new IllegalArgumentException("Invalid resultCode: " + code);
    }

}
