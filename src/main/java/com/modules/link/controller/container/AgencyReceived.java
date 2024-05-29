package com.modules.link.controller.container;

import lombok.Getter;
import lombok.ToString;

@Getter
public class AgencyReceived {
    private String agencyId;
    private String msgType;
    private String encryptData;
    private String verifyInfo;
}
