package com.modules.pg.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modules.pg.model.HFDataModel;
import com.modules.pg.model.HFResultDataModel;
import com.modules.pg.service.HFResultService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping(value = {"/agency/payment/api/result", "/payment/api/result"})
public class HFResultController {
    private final HFResultService hfResultService;

    @Value("${external.url}")
    private String profileSpecificUrl;

    @Value("${external.payment.url}")
    private String profileSpecificPaymentUrl;


    public HFResultController(HFResultService hfResultService) {
        this.hfResultService = hfResultService;
    }

    // 헥토파이낸셜 서버 요청, 현 서버 수신 - 로컬 사용 불가
    @PostMapping(value = "/noti")
    public String noti(HttpServletRequest request) {
        //TODO
        // Noti 로그용
        Set<String> keySet = request.getParameterMap().keySet();
        for (String key : keySet) {
            System.out.println("[Noti] : " + key + ": " + request.getParameter(key));
        }

        HFDataModel notiCA = new HFDataModel(request.getParameter("outStatCd"), request.getParameter("trdNo"), request.getParameter("method"), request.getParameter("bizType"), request.getParameter("mchtId"), request.getParameter("mchtTrdNo"), request.getParameter("mchtCustNm"), request.getParameter("mchtName"), request.getParameter("pmtprdNm"), request.getParameter("trdDtm"), request.getParameter("trdAmt"), request.getParameter("billKey"), request.getParameter("billKeyExpireDt"), request.getParameter("bankCd"), request.getParameter("bankNm"), request.getParameter("cardCd"), request.getParameter("cardNm"), request.getParameter("telecomCd"), request.getParameter("telecomNm"), request.getParameter("vAcntNo"), request.getParameter("expireDt"), request.getParameter("AcntPrintNm"), request.getParameter("dpstrNm"), request.getParameter("email"), request.getParameter("mchtCustId"), request.getParameter("cardNo"), request.getParameter("cardApprNo"), request.getParameter("instmtMon"), request.getParameter("instmtType"), request.getParameter("phoneNoEnc"), request.getParameter("orgTrdNo"), request.getParameter("orgTrdDt"), request.getParameter("mixTrdNo"), request.getParameter("mixTrdAmt"), request.getParameter("payAmt"), request.getParameter("csrcIssNo"), request.getParameter("cnclType"), request.getParameter("mchtParam"), request.getParameter("pktHash"));
        HFDataModel notiVA = new HFDataModel(request.getParameter("outStatCd"), request.getParameter("trdNo"), request.getParameter("method"), request.getParameter("bizType"), request.getParameter("mchtId"), request.getParameter("mchtTrdNo"), request.getParameter("mchtCustNm"), request.getParameter("mchtName"), request.getParameter("pmtprdNm"), request.getParameter("trdDtm"), request.getParameter("trdAmt"), request.getParameter("bankCd"), request.getParameter("bankNm"), request.getParameter("acntType"), request.getParameter("vAcntNo"), request.getParameter("expireDt"), request.getParameter("AcntPrintNm"), request.getParameter("dpstrNm"), request.getParameter("email"), request.getParameter("mchtCustId"), request.getParameter("orgTrdNo"), request.getParameter("orgTrdDt"), request.getParameter("csrcIssNo"), request.getParameter("cnclType"), request.getParameter("mchtParam"), request.getParameter("pktHash"));
        /** 응답 파라미터 세팅 */
        String method = request.getParameter("method");
        if (method.equals("CA")) {
            return hfResultService.notiCAData(notiCA);
        }
        if (method.equals("VA")) {
            return hfResultService.notiVAData(notiVA);
        }
        return "FAIL";
    }

    // 결과 페이지 이후 콜백 S2S 안돼서 임시 처리
    @PostMapping(value = "/next")
    public void next(HttpServletRequest request, HttpServletResponse response) throws IOException {

        //TODO
        // Next 로그용
        Set<String> keySet = request.getParameterMap().keySet();
        for (String key : keySet) {
            System.out.println("[Next] : " + key + ": " + request.getParameter(key));
        }

        String method = request.getParameter("method");
        HFDataModel card = new HFDataModel(request.getParameter("mchtTrdNo"), request.getParameter("pointTrdNo"), request.getParameter("trdNo"), request.getParameter("vtlAcntNo"), request.getParameter("mchtCustId"), request.getParameter("fnNm"), request.getParameter("method"), request.getParameter("authNo"), request.getParameter("trdAmt"), request.getParameter("pointTrdAmt"), request.getParameter("cardTrdAmt"), request.getParameter("outRsltMsg"), request.getParameter("mchtParam"), request.getParameter("outStatCd"), request.getParameter("outRsltCd"), request.getParameter("intMon"), request.getParameter("authDt"), request.getParameter("mchtId"), request.getParameter("fnCd"));
        HFDataModel vbank = new HFDataModel(request.getParameter("mchtTrdNo"), request.getParameter("trdNo"), request.getParameter("vtlAcntNo"), request.getParameter("mchtCustId"), request.getParameter("fnNm"), request.getParameter("method"), request.getParameter("trdAmt"), request.getParameter("outRsltMsg"), request.getParameter("mchtParam"), request.getParameter("expireDt"), request.getParameter("outStatCd"), request.getParameter("outRsltCd"), request.getParameter("authDt"), request.getParameter("mchtId"), request.getParameter("fnCd"));

        if (method.equals("card")) {
            hfResultService.nextCardData(card);
        }
        if (method.equals("vbank")) {
            hfResultService.nextVBankData(vbank);
        }

        Map<String, String> res_params = new LinkedHashMap<>();

        String[] pairs = request.getParameter("mchtParam").split("&");
        String autopayYN;
        Map<String, String> parseParams = parseParams(pairs);
        res_params.put("companyName",parseParams.get("companyName"));
        res_params.put("bizNumber",parseParams.get("bizNumber"));
        res_params.put("productName",parseParams.get("productName"));
        res_params.put("startDate",parseParams.get("startDate"));
        res_params.put("autopayYN",parseParams.get("autopayYN"));

        ObjectMapper objectMapper = new ObjectMapper();
        String data = Base64.getEncoder().encodeToString(objectMapper.writeValueAsString(res_params).getBytes());

        response.sendRedirect(profileSpecificPaymentUrl + "/agency/procpayment/end.html?data=" + data);
    }


    @PostMapping(value = "/cancel")
    public void cancel(HttpServletResponse response) throws IOException {
        response.sendRedirect(profileSpecificPaymentUrl+ "/agency/procpayment/cancel.html");
    }

    @PostMapping(value = "/decrypt")
    public String result(HttpServletRequest request, @RequestBody Map<String, String> requestMap) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String encodeData = requestMap.get("data");
        String decodeData = new String(Base64.getDecoder().decode(encodeData.getBytes()), StandardCharsets.UTF_8);

        Map<String, String> resMap = objectMapper.readValue(decodeData, new TypeReference<>() {
        });

        if (resMap == null) {
            HFResultDataModel resultDataModel = new HFResultDataModel();
            resultDataModel.setMchtId(request.getParameter("mchtId"));
            resultDataModel.setOutStatCd(request.getParameter("outStatCd"));
            resultDataModel.setOutRsltCd(request.getParameter("outRsltCd"));
            resultDataModel.setOutRsltMsg(request.getParameter("outRsltMsg"));
            resultDataModel.setMethod(request.getParameter("method"));
            resultDataModel.setMchtTrdNo(request.getParameter("mchtTrdNo"));
            resultDataModel.setMchtCustId(request.getParameter("mchtCustId"));
            resultDataModel.setTrdNo(request.getParameter("trdNo"));
            resultDataModel.setTrdAmt(request.getParameter("trdAmt"));
            resultDataModel.setMchtParam(request.getParameter("mchtParam"));
            resultDataModel.setAuthDt(request.getParameter("authDt"));
            resultDataModel.setAuthNo(request.getParameter("authNo"));
            resultDataModel.setReqIssueDt(request.getParameter("reqIssueDt"));
            resultDataModel.setIntMon(request.getParameter("intMon"));
            resultDataModel.setFnNm(request.getParameter("fnNm"));
            resultDataModel.setFnCd(request.getParameter("fnCd"));
            resultDataModel.setPointTrdNo(request.getParameter("pointTrdNo"));
            resultDataModel.setPointTrdAmt(request.getParameter("pointTrdAmt"));
            resultDataModel.setCardTrdAmt(request.getParameter("cardTrdAmt"));
            resultDataModel.setVtlAcntNo(request.getParameter("vtlAcntNo"));
            resultDataModel.setExpireDt(request.getParameter("expireDt"));
            resultDataModel.setCphoneNo(request.getParameter("cphoneNo"));
            resultDataModel.setBillKey(request.getParameter("billKey"));
            resultDataModel.setCsrcAmt(request.getParameter("csrcAmt"));
            resMap = hfResultService.decryptData(resultDataModel);
        }
        return objectMapper.writeValueAsString(resMap);
    }


    private Map<String, String> parseParams(String[] pairs) {
        Map<String, String> parsedParams = new HashMap<>();
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                parsedParams.put(keyValue[0], keyValue[1]);
            }
        }
        return parsedParams;
    }

}


