package com.modules.payment.domain;

import com.modules.payment.application.exceptions.enums.EnumResultCode;

import java.util.HashMap;

public class PGResponseManager {
    private static final String RESULT_CODE = EnumResultCode.SUCCESS.getCode();
    private static final String RESULT_MESSAGE = EnumResultCode.SUCCESS.getValue();

    private String resultCode;
    private String resultMsg;
    private String mchtId;
    private String method;
    private String trdDt;
    private String trdTm;

    private String mchtTrdNo;
    private String trdAmt;
    private String hashCipher;
    private HashMap<String, String> encParams;

    public PGResponseManager(String mchtId, String method, String mchtTrdNo, String trdAmt, String trdDt, String trdTm, String hashCipher, HashMap<String, String> encParams) {
        this.resultCode = RESULT_CODE;
        this.resultMsg = RESULT_MESSAGE;
        this.mchtId = mchtId;
        this.method = method;
        this.mchtTrdNo = mchtTrdNo;
        this.trdAmt = trdAmt;
        this.trdDt = trdDt;
        this.trdTm = trdTm;
        this.hashCipher = hashCipher;
        this.encParams = encParams;
    }

}
