package com.modules.adapter.in.models;

import com.modules.application.exceptions.enums.EnumResultCode;

public class ResponseManager {
    private static final String RESULT_CODE = EnumResultCode.SUCCESS.getCode();
    private static final String RESULT_MESSAGE = EnumResultCode.SUCCESS.getValue();
    String resultCode;
    String resultMsg;
    String msgType;
    String encryptData;
    String verifyInfo;

    public ResponseManager(String type, String data, String verify) {
        this.resultCode = RESULT_CODE;
        this.resultMsg = RESULT_MESSAGE;
        this.msgType = type;
        this.encryptData = data;
        this.verifyInfo = verify;
    }

    public ResponseManager(String code, String msg) {
        this.resultCode = code;
        this.resultMsg = msg;

    }
}
