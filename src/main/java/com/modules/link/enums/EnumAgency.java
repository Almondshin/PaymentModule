package com.modules.link.enums;

import lombok.Getter;

@Getter
public enum EnumAgency {
    SQUARES("squares", "스퀘어스", "SQ"),
    DREAMTEST("dreamtest", "드림테스트", "DT");

    private final String SITE_STATUS = "SiteStatus";
    private final String SITE_INFO = "SiteInfo";
    private final String REG_INFO = "RegAgencySiteInfo";
    private final String CANCEL = "CancelSiteInfo";
    private final String PAYMENT_INFO = "NotifyPaymentSiteInfo";
    private final String NOTIFICATION = "NotifyStatusSite";

    private final String code;
    private final String value;
    private final String initial;

    EnumAgency(String code, String value, String initial) {
        this.code = code;
        this.value = value;
        this.initial = initial;
    }


    public static String getMsgType(String agencyId, String message) {
        for (EnumAgency enumAgency : EnumAgency.values()) {
            if (enumAgency.getCode().equals(agencyId)) {
                switch (message) {
                    case "info":
                        return enumAgency.SITE_INFO;
                    case "status":
                        return enumAgency.SITE_STATUS;
                    case "reg":
                        return enumAgency.REG_INFO;
                    case "cancel":
                        return enumAgency.CANCEL;
                    case "payment":
                        return enumAgency.PAYMENT_INFO;
                    case "notify":
                        return enumAgency.NOTIFICATION;
                    default:
                        throw new IllegalStateException("존재하지 않는 messageType 입니다. 버전을 확인해주세요. " + message);
                }
            }
        }
        throw new IllegalStateException("해당 agencyId를 가진 EnumAgency가 존재하지 않습니다. " + agencyId);
    }
}