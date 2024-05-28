package com.modules.link.controller.container;

import com.modules.link.enums.EnumAgency;
import lombok.Getter;


@Getter
public class PaymentReceived {
    private String agencyId;
    private String siteId;
    private String rateSel;
    private String startDate;

    public void validData() {
        if (agencyId == null || agencyId.isEmpty() || siteId == null || siteId.isEmpty()) {
            throw new NullPointerException("agencyId or siteId is null or empty");
        }
    }
    public String getSiteId() {
        for (EnumAgency agency : EnumAgency.values()) {
            if (agency.getCode().equals(agencyId)) {
                if (siteId.toUpperCase().startsWith(agency.getInitial())) {
                    if (siteId.length() > 10) {
                        throw new IllegalArgumentException("'" + siteId + "' 사이트 아이디는 10자리 이하여야 합니다.");
                    }
                    return siteId;
                } else {
                    throw new IllegalArgumentException("'" + siteId + "' 사이트 아이디는 제휴사의 Initial로 시작되어야 합니다.");
                }
            }
        }
        throw new IllegalArgumentException("No matching agency for the provided agencyId: " + agencyId);
    }
}