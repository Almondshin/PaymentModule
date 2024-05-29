package com.modules.link.controller.container;

import com.modules.link.enums.EnumResultCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AgencyResponse {

    private String resultCode;
    private String resultMsg;
    private String msgType;
    private String encryptData;
    private String verifyInfo;

    public AgencyResponse(){
        this.resultCode = EnumResultCode.SUCCESS.getCode();
        this.resultMsg = EnumResultCode.SUCCESS.getMessage();
    }
    public AgencyResponse(String encryptData, String verifyInfo, String messageType) {
        this.resultCode = EnumResultCode.SUCCESS.getCode();
        this.resultMsg = EnumResultCode.SUCCESS.getMessage();
        this.msgType = messageType;
        this.encryptData = encryptData;
        this.verifyInfo = verifyInfo;
    }

    public AgencyResponse(EnumResultCode resultCode) {
        this.resultCode = resultCode.getCode();
        this.resultMsg = resultCode.getMessage();
    }

    public AgencyResponse(String code, String message) {
        this.resultCode = code;
        this.resultMsg = message;
    }


}