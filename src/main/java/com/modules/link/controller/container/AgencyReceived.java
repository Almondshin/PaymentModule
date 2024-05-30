package com.modules.link.controller.container;

import lombok.Getter;

@Getter
public class AgencyReceived {
    private String agencyId;
    private String msgType;
    private String encryptData;
    private String verifyInfo;
}
