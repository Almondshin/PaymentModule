package com.modules.application.exceptions.exceptions.handler;

import com.modules.application.exceptions.enums.EnumResultCode;
import lombok.Getter;

@Getter
public class ResponseMessage {
    private final String resultCode;
    private final String resultMsg;
//    private final String siteStatus;

    public ResponseMessage(String resultCode, String resultMsg) {
        this.resultCode = EnumResultCode.fromCode(resultCode);
        this.resultMsg = resultMsg;
//        this.siteStatus = EnumSiteStatus.fromCode(statusCode);
    }
}