package com.modules.pg.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class HFDataModel {
    private String mchtTrdNo;
    private String pointTrdNo;
    private String trdNo;
    private String vtlAcntNo;
    private String mchtCustId;
    private String fnNm;
    private String method;
    private String authNo;
    private String trdAmt;
    private String pointTrdAmt;
    private String cardTrdAmt;
    private String outRsltMsg;
    private String mchtParam;
    private String outStatCd;
    private String outRsltCd;
    private String intMon;
    private String authDt;
    private String mchtId;
    private String fnCd;
    private String expireDt;


    private String bizType;
    private String mchtCustNm;
    private String mchtName;
    private String pmtprdNm;
    private String trdDtm;
    private String bankCd;
    private String bankNm;
    private String acntType;
    private String vAcntNo;
    private String AcntPrintNm;
    private String dpstrNm;
    private String email;
    private String orgTrdNo;
    private String orgTrdDt;
    private String csrcIssNo;
    private String cnclType;
    private String pktHash;

    private String billKey;
    private String billKeyExpireDt;
    private String cardCd;
    private String cardNm;
    private String telecomCd;
    private String telecomNm;
    private String cardNo;
    private String cardApprNo;
    private String instmtMon;
    private String instmtType;
    private String phoneNoEnc;
    private String mixTrdNo;
    private String mixTrdAmt;
    private String payAmt;

    // Hashplain 생성자
    public HFDataModel(String mchtTrdNo, String trdAmt, String outStatCd, String mchtId, String trdDtm) {
        this.mchtTrdNo = mchtTrdNo;
        this.trdAmt = trdAmt;
        this.outStatCd = outStatCd;
        this.mchtId = mchtId;
        this.trdDtm = trdDtm;
    }

    // next card 생성자
    public HFDataModel(String mchtTrdNo, String pointTrdNo, String trdNo, String vtlAcntNo, String mchtCustId, String fnNm, String method, String authNo, String trdAmt, String pointTrdAmt, String cardTrdAmt, String outRsltMsg, String mchtParam, String outStatCd, String outRsltCd, String intMon, String authDt, String mchtId, String fnCd) {
        this.mchtTrdNo = mchtTrdNo;
        this.pointTrdNo = pointTrdNo;
        this.trdNo = trdNo;
        this.vtlAcntNo = vtlAcntNo;
        this.mchtCustId = mchtCustId;
        this.fnNm = fnNm;
        this.method = method;
        this.authNo = authNo;
        this.trdAmt = trdAmt;
        this.pointTrdAmt = pointTrdAmt;
        this.cardTrdAmt = cardTrdAmt;
        this.outRsltMsg = outRsltMsg;
        this.mchtParam = mchtParam;
        this.outStatCd = outStatCd;
        this.outRsltCd = outRsltCd;
        this.intMon = intMon;
        this.authDt = authDt;
        this.mchtId = mchtId;
        this.fnCd = fnCd;
    }

    // next vBank 생성자
    public HFDataModel(String mchtTrdNo, String trdNo, String vtlAcntNo, String mchtCustId, String fnNm, String method, String trdAmt, String outRsltMsg, String mchtParam, String expireDt, String outStatCd, String outRsltCd, String authDt, String mchtId, String fnCd){
        this.mchtTrdNo = mchtTrdNo;
        this.trdNo = trdNo;
        this.vtlAcntNo = vtlAcntNo;
        this.mchtCustId = mchtCustId;
        this.fnNm = fnNm;
        this.method = method;
        this.trdAmt = trdAmt;
        this.outRsltMsg = outRsltMsg;
        this.mchtParam = mchtParam;
        this.expireDt = expireDt;
        this.outStatCd = outStatCd;
        this.outRsltCd = outRsltCd;
        this.authDt = authDt;
        this.mchtId = mchtId;
        this.fnCd = fnCd;
    }

    // noti CA 생성자
    public HFDataModel(String outStatCd, String trdNo, String method, String bizType, String mchtId, String mchtTrdNo, String mchtCustNm, String mchtName, String pmtprdNm, String trdDtm, String trdAmt, String billKey, String billKeyExpireDt, String bankCd, String bankNm, String cardCd, String cardNm, String telecomCd, String telecomNm, String vAcntNo, String expireDt, String AcntPrintNm, String dpstrNm, String email, String mchtCustId, String cardNo, String cardApprNo, String instmtMon, String instmtType, String phoneNoEnc, String orgTrdNo, String orgTrdDt, String mixTrdNo, String mixTrdAmt, String payAmt, String csrcIssNo, String cnclType, String mchtParam, String pktHash){
        this.outStatCd = outStatCd;
        this.trdNo = trdNo;
        this.method = method;
        this.bizType = bizType;
        this.mchtId = mchtId;
        this.mchtTrdNo = mchtTrdNo;
        this.mchtCustNm = mchtCustNm;
        this.mchtName = mchtName;
        this.pmtprdNm = pmtprdNm;
        this.trdDtm = trdDtm;
        this.trdAmt = trdAmt;
        this.billKey = billKey;
        this.billKeyExpireDt = billKeyExpireDt;
        this.bankCd = bankCd;
        this.bankNm = bankNm;
        this.cardCd = cardCd;
        this.cardNm = cardNm;
        this.telecomCd = telecomCd;
        this.telecomNm = telecomNm;
        this.vAcntNo = vAcntNo;
        this.expireDt = expireDt;
        this.AcntPrintNm = AcntPrintNm;
        this.dpstrNm = dpstrNm;
        this.email = email;
        this.mchtCustId = mchtCustId;
        this.cardNo = cardNo;
        this.cardApprNo = cardApprNo;
        this.instmtMon = instmtMon;
        this.instmtType = instmtType;
        this.phoneNoEnc = phoneNoEnc;
        this.orgTrdNo = orgTrdNo;
        this.orgTrdDt = orgTrdDt;
        this.mixTrdNo = mixTrdNo;
        this.mixTrdAmt = mixTrdAmt;
        this.payAmt = payAmt;
        this.csrcIssNo = csrcIssNo;
        this.cnclType = cnclType;
        this.mchtParam = mchtParam;
        this.pktHash = pktHash;
    }

    // noti VA 생성자
    public HFDataModel(String outStatCd, String trdNo, String method, String bizType, String mchtId, String mchtTrdNo, String mchtCustNm, String mchtName, String pmtprdNm, String trdDtm, String trdAmt, String bankCd, String bankNm, String acntType, String vAcntNo, String expireDt, String AcntPrintNm, String dpstrNm, String email, String mchtCustId, String orgTrdNo, String orgTrdDt, String csrcIssNo, String cnclType, String mchtParam, String pktHash){
        this.outStatCd = outStatCd;
        this.trdNo = trdNo;
        this.method = method;
        this.bizType = bizType;
        this.mchtId = mchtId;
        this.mchtTrdNo = mchtTrdNo;
        this.mchtCustNm = mchtCustNm;
        this.mchtName = mchtName;
        this.pmtprdNm = pmtprdNm;
        this.trdDtm = trdDtm;
        this.trdAmt = trdAmt;
        this.bankCd = bankCd;
        this.bankNm = bankNm;
        this.acntType = acntType;
        this.vAcntNo = vAcntNo;
        this.expireDt = expireDt;
        this.AcntPrintNm = AcntPrintNm;
        this.dpstrNm = dpstrNm;
        this.email = email;
        this.mchtCustId = mchtCustId;
        this.orgTrdNo = orgTrdNo;
        this.orgTrdDt = orgTrdDt;
        this.csrcIssNo = csrcIssNo;
        this.cnclType = cnclType;
        this.mchtParam = mchtParam;
        this.pktHash = pktHash;
    }

    public String getHashPlain() {
        return this.outStatCd + this.trdDtm + this.mchtId + this.mchtTrdNo + this.trdAmt;
    }

}
