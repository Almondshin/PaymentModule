package com.modules.adapter.in.models;

import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class PaymentDataContainer {
    private String mchtId;
    private String method;
    private String mchtTrdNo;
    private String trdDt;
    private String trdTm;
    private String mchtParam;
    private String plainTrdAmt;
    private String plainMchtCustNm;
    private String plainCphoneNo;
    private String plainEmail;
    private String plainMchtCustId;
    private String plainTaxAmt;
    private String plainVatAmt;
    private String plainTaxFreeAmt;
    private String plainSvcAmt;
    private String plainClipCustNm;
    private String plainClipCustCi;
    private String plainClipCustPhoneNo;

    public PaymentDataContainer(String mchtId, String method, String mchtTrdNo, String trdDt, String trdTm, String plainTrdAmt) {
        this.mchtId = mchtId;
        this.method = method;
        this.mchtTrdNo = mchtTrdNo;
        this.trdDt = trdDt;
        this.trdTm = trdTm;
        this.plainTrdAmt = plainTrdAmt;
    }

    public PaymentDataContainer(String plainTrdAmt, String plainMchtCustNm, String plainCphoneNo, String plainEmail, String plainMchtCustId, String plainTaxAmt, String plainVatAmt, String plainTaxFreeAmt, String plainSvcAmt, String plainClipCustNm, String plainClipCustCi, String plainClipCustPhoneNo, String mchtParam) {
        this.plainTrdAmt = plainTrdAmt;
        this.plainMchtCustNm = plainMchtCustNm;
        this.plainCphoneNo = plainCphoneNo;
        this.plainEmail = plainEmail;
        this.plainMchtCustId = plainMchtCustId;
        this.plainTaxAmt = plainTaxAmt;
        this.plainVatAmt = plainVatAmt;
        this.plainTaxFreeAmt = plainTaxFreeAmt;
        this.plainSvcAmt = plainSvcAmt;
        this.plainClipCustNm = plainClipCustNm;
        this.plainClipCustCi = plainClipCustCi;
        this.plainClipCustPhoneNo = plainClipCustPhoneNo;
        this.mchtParam = mchtParam;
    }

    public String getHashPlain() {
        return this.mchtId+this.method+this.mchtTrdNo+this.trdDt+this.trdTm+this.plainTrdAmt;
    }
}
