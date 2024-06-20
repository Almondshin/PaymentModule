package com.modules.link.infrastructure.hectofinancial.api.mapper;


import com.modules.link.infrastructure.hectofinancial.dto.HFDtos.*;

import javax.servlet.http.HttpServletRequest;

public class NotiMapper {

    private static String getParameter(HttpServletRequest request, String parameterName) {
        return request.getParameter(parameterName);
    }

    public static NotiCADto buildNotiCADto(HttpServletRequest request) {
        return NotiCADto.builder()
                .outStatCd(getParameter(request, "outStatCd"))
                .trdNo(getParameter(request, "trdNo"))
                .method(getParameter(request, "method"))
                .bizType(getParameter(request, "bizType"))
                .mchtId(getParameter(request, "mchtId"))
                .mchtTrdNo(getParameter(request, "mchtTrdNo"))
                .mchtCustNm(getParameter(request, "mchtCustNm"))
                .mchtName(getParameter(request, "mchtName"))
                .pmtprdNm(getParameter(request, "pmtprdNm"))
                .trdDtm(getParameter(request, "trdDtm"))
                .trdAmt(getParameter(request, "trdAmt"))
                .billKey(getParameter(request, "billKey"))
                .billKeyExpireDt(getParameter(request, "billKeyExpireDt"))
                .bankCd(getParameter(request, "bankCd"))
                .bankNm(getParameter(request, "bankNm"))
                .cardCd(getParameter(request, "cardCd"))
                .cardNm(getParameter(request, "cardNm"))
                .telecomCd(getParameter(request, "telecomCd"))
                .telecomNm(getParameter(request, "telecomNm"))
                .vAcntNo(getParameter(request, "vAcntNo"))
                .expireDt(getParameter(request, "expireDt"))
                .AcntPrintNm(getParameter(request, "AcntPrintNm"))
                .dpstrNm(getParameter(request, "dpstrNm"))
                .email(getParameter(request, "email"))
                .mchtCustId(getParameter(request, "mchtCustId"))
                .cardNo(getParameter(request, "cardNo"))
                .cardApprNo(getParameter(request, "cardApprNo"))
                .instmtMon(getParameter(request, "instmtMon"))
                .instmtType(getParameter(request, "instmtType"))
                .phoneNoEnc(getParameter(request, "phoneNoEnc"))
                .orgTrdNo(getParameter(request, "orgTrdNo"))
                .orgTrdDt(getParameter(request, "orgTrdDt"))
                .mixTrdNo(getParameter(request, "mixTrdNo"))
                .mixTrdAmt(getParameter(request, "mixTrdAmt"))
                .payAmt(getParameter(request, "payAmt"))
                .csrcIssNo(getParameter(request, "csrcIssNo"))
                .cnclType(getParameter(request, "cnclType"))
                .mchtParam(getParameter(request, "mchtParam"))
                .pktHash(getParameter(request, "pktHash"))
                .build();
    }

    public static NotiVADto buildNotiVADto(HttpServletRequest request) {
        return NotiVADto.builder()
                .outStatCd(getParameter(request, "outStatCd"))
                .trdNo(getParameter(request, "trdNo"))
                .method(getParameter(request, "method"))
                .bizType(getParameter(request, "bizType"))
                .mchtId(getParameter(request, "mchtId"))
                .mchtTrdNo(getParameter(request, "mchtTrdNo"))
                .mchtCustNm(getParameter(request, "mchtCustNm"))
                .mchtName(getParameter(request, "mchtName"))
                .pmtprdNm(getParameter(request, "pmtprdNm"))
                .trdDtm(getParameter(request, "trdDtm"))
                .trdAmt(getParameter(request, "trdAmt"))
                .bankCd(getParameter(request, "bankCd"))
                .bankNm(getParameter(request, "bankNm"))
                .acntType(getParameter(request, "acntType"))
                .vAcntNo(getParameter(request, "vAcntNo"))
                .expireDt(getParameter(request, "expireDt"))
                .AcntPrintNm(getParameter(request, "AcntPrintNm"))
                .dpstrNm(getParameter(request, "dpstrNm"))
                .email(getParameter(request, "email"))
                .mchtCustId(getParameter(request, "mchtCustId"))
                .orgTrdNo(getParameter(request, "orgTrdNo"))
                .orgTrdDt(getParameter(request, "orgTrdDt"))
                .csrcIssNo(getParameter(request, "csrcIssNo"))
                .cnclType(getParameter(request, "cnclType"))
                .mchtParam(getParameter(request, "mchtParam"))
                .pktHash(getParameter(request, "pktHash"))
                .build();
    }

}