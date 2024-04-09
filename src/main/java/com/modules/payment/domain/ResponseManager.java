package com.modules.payment.domain;

import com.modules.payment.application.exceptions.enums.EnumResultCode;

import java.util.List;
import java.util.Map;

public class ResponseManager {
    private static final String RESULT_CODE = EnumResultCode.SUCCESS.getCode();
    private static final String RESULT_MESSAGE = EnumResultCode.SUCCESS.getValue();
    private final String resultCode;
    private final String resultMsg;
    private String msgType;
    private String encryptData;
    private String verifyInfo;

    private List<String> clientInfo;
    private String rateSel;
    private String startDate;
    private String extensionStatus;
    private int excessCount;
    private int excessAmount;
    private String profileUrl;
    private String profilePaymentUrl;
    private List<Map<String, String>> listSel;

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

    public ResponseManager(List<String> clientInfo, String rateSel, String startDate, String profileUrl, String profilePaymentUrl, List<Map<String, String>> listSel, boolean isExtendable, String extensionStatus, int excessCount, int excessAmount) {
        this.resultCode = RESULT_CODE;
        this.resultMsg = RESULT_MESSAGE;
        this.clientInfo = clientInfo;
        this.rateSel = rateSel;
        this.startDate = startDate;
        this.profileUrl = profileUrl;
        this.profilePaymentUrl = profilePaymentUrl;
        this.listSel = listSel;
        if (isExtendable) {
            this.extensionStatus = extensionStatus;
            this.excessCount = excessCount;
            this.excessAmount = excessAmount;
        }
    }

}
