package com.modules.application.domain;

import lombok.Getter;

@Getter
public class Agency {
    private final String agencyId;
    private final String siteId;

    public Agency(String agencyId, String siteId) {
        this.agencyId = agencyId;
        this.siteId = siteId;
    }
}
