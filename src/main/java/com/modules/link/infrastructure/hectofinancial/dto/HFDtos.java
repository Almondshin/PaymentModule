package com.modules.link.infrastructure.hectofinancial.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class HFDtos {

    // Hashplain DTO
    @Getter
    @ToString
    @Builder
    public static class HashplainDto {
        private String mchtTrdNo;
        private String trdAmt;
        private String outStatCd;
        private String mchtId;
        private String trdDtm;
    }

    // NextCard DTO
    @Getter
    @ToString
    @Builder
    public static class NextCardDto {
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
    }

    // NextVBank DTO
    @Getter
    @ToString
    @Builder
    public static class NextVBankDto {
        private String mchtTrdNo;
        private String trdNo;
        private String vtlAcntNo;
        private String mchtCustId;
        private String fnNm;
        private String method;
        private String trdAmt;
        private String outRsltMsg;
        private String mchtParam;
        private String expireDt;
        private String outStatCd;
        private String outRsltCd;
        private String authDt;
        private String mchtId;
        private String fnCd;
    }

    // NotiCADto DTO
    @Getter
    @ToString
    @Builder
    public static class NotiCADto {
        private String outStatCd;
        private String trdNo;
        private String method;
        private String bizType;
        private String mchtId;
        private String mchtTrdNo;
        private String mchtCustNm;
        private String mchtName;
        private String pmtprdNm;
        private String trdDtm;
        private String trdAmt;
        private String billKey;
        private String billKeyExpireDt;
        private String bankCd;
        private String bankNm;
        private String cardCd;
        private String cardNm;
        private String telecomCd;
        private String telecomNm;
        private String vAcntNo;
        private String expireDt;
        private String AcntPrintNm;
        private String dpstrNm;
        private String email;
        private String mchtCustId;
        private String cardNo;
        private String cardApprNo;
        private String instmtMon;
        private String instmtType;
        private String phoneNoEnc;
        private String orgTrdNo;
        private String orgTrdDt;
        private String mixTrdNo;
        private String mixTrdAmt;
        private String payAmt;
        private String csrcIssNo;
        private String cnclType;
        private String mchtParam;
        private String pktHash;
    }

    // NotiVADto DTO
    @Getter
    @ToString
    @Builder
    public static class NotiVADto {
        private String outStatCd;
        private String trdNo;
        private String method;
        private String bizType;
        private String mchtId;
        private String mchtTrdNo;
        private String mchtCustNm;
        private String mchtName;
        private String pmtprdNm;
        private String trdDtm;
        private String trdAmt;
        private String bankCd;
        private String bankNm;
        private String acntType;
        private String vAcntNo;
        private String expireDt;
        private String AcntPrintNm;
        private String dpstrNm;
        private String email;
        private String mchtCustId;
        private String orgTrdNo;
        private String orgTrdDt;
        private String csrcIssNo;
        private String cnclType;
        private String mchtParam;
        private String pktHash;
    }


}
