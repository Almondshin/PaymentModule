package com.modules.link.controller.container;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.modules.link.enums.EnumResultCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgencyResponse {
    private final String resultCode;
    private final String resultMsg;
    private String msgType;
    private String encryptData;
    private String verifyInfo;

    public AgencyResponse(){
        this.resultCode = EnumResultCode.SUCCESS.getCode();
        this.resultMsg = EnumResultCode.SUCCESS.getMessage();
    }

    @Builder
    public AgencyResponse(String encryptData, String verifyInfo, String messageType) {
        this.resultCode = EnumResultCode.SUCCESS.getCode();
        this.resultMsg = EnumResultCode.SUCCESS.getMessage();
        this.msgType = messageType;
        this.encryptData = encryptData;
        this.verifyInfo = verifyInfo;
    }

    public AgencyResponse(String code, String message) {
        this.resultCode = code;
        this.resultMsg = message;
    }

}