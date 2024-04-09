package com.modules.pg.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modules.payment.application.domain.Agency;
import com.modules.payment.application.domain.AgencyInfoKey;
import com.modules.payment.application.domain.Client;
import com.modules.payment.application.domain.PaymentHistory;
import com.modules.payment.application.enums.EnumAgency;
import com.modules.payment.application.enums.EnumExtraAmountStatus;
import com.modules.payment.application.enums.EnumPaymentStatus;
import com.modules.payment.application.enums.EnumTradeTrace;
import com.modules.payment.application.port.in.EncryptUseCase;
import com.modules.payment.application.port.in.NotiUseCase;
import com.modules.payment.application.port.out.load.LoadEncryptDataPort;
import com.modules.payment.application.port.out.load.LoadPaymentDataPort;
import com.modules.payment.application.port.out.save.SaveAgencyDataPort;
import com.modules.payment.application.port.out.save.SavePaymentDataPort;
import com.modules.pg.hectofinancial.Constant;
import com.modules.pg.model.HFDataModel;
import com.modules.pg.model.HFResultDataModel;
import com.modules.pg.utils.EncryptUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Hf result service.
 */
@Service
public class HFResultService {
    private final Constant constant;
    private final SavePaymentDataPort savePaymentDataPort;
    private final SaveAgencyDataPort saveAgencyDataPort;
    private final LoadEncryptDataPort loadEncryptDataPort;
    private final LoadPaymentDataPort loadPaymentDataPort;
    private final NotiUseCase notiUseCase;
    private final EncryptUseCase encryptUseCase;

    @Value("${external.admin.url}")
    private String profileSpecificAdminUrl;

    Logger logger = LoggerFactory.getLogger("HFResultController");

    public HFResultService(Constant constant, SavePaymentDataPort savePaymentDataPort, SaveAgencyDataPort saveAgencyDataPort, LoadEncryptDataPort loadEncryptDataPort, LoadPaymentDataPort loadPaymentDataPort, NotiUseCase notiUseCase, EncryptUseCase encryptUseCase) {
        this.constant = constant;
        this.savePaymentDataPort = savePaymentDataPort;
        this.saveAgencyDataPort = saveAgencyDataPort;
        this.loadEncryptDataPort = loadEncryptDataPort;
        this.loadPaymentDataPort = loadPaymentDataPort;
        this.notiUseCase = notiUseCase;
        this.encryptUseCase = encryptUseCase;
    }

    public String nextCardData(HFDataModel hfDataModel) throws IOException {
        Map<String, String> RES_PARAMS = new LinkedHashMap<>();
        RES_PARAMS.put("mchtTrdNo", hfDataModel.getMchtTrdNo());
        RES_PARAMS.put("pointTrdNo", hfDataModel.getPointTrdNo());
        RES_PARAMS.put("trdNo", hfDataModel.getTrdNo());
        RES_PARAMS.put("vtlAcntNo", hfDataModel.getVtlAcntNo());
        RES_PARAMS.put("mchtCustId", hfDataModel.getMchtCustId());
        RES_PARAMS.put("fnNm", hfDataModel.getFnNm());
        RES_PARAMS.put("method", hfDataModel.getMethod());
        RES_PARAMS.put("authNo", hfDataModel.getAuthNo());
        RES_PARAMS.put("trdAmt", hfDataModel.getTrdAmt());
        RES_PARAMS.put("pointTrdAmt", hfDataModel.getPointTrdAmt());
        RES_PARAMS.put("cardTrdAmt", hfDataModel.getCardTrdAmt());
        RES_PARAMS.put("outRsltMsg", hfDataModel.getOutRsltMsg());
        RES_PARAMS.put("mchtParam", hfDataModel.getMchtParam());
        RES_PARAMS.put("outStatCd", hfDataModel.getOutStatCd());
        RES_PARAMS.put("outRsltCd", hfDataModel.getOutRsltCd());
        RES_PARAMS.put("intMon", hfDataModel.getIntMon());
        RES_PARAMS.put("authDt", hfDataModel.getAuthDt());
        RES_PARAMS.put("mchtId", hfDataModel.getMchtId());
        RES_PARAMS.put("fnCd", hfDataModel.getFnCd());
        ObjectMapper objectMapper = new ObjectMapper();
        return Base64.getEncoder().encodeToString(objectMapper.writeValueAsString(RES_PARAMS).getBytes());
    }

    public String nextVBankData(HFDataModel hfDataModel) throws IOException {
        Map<String, String> RES_PARAMS = new LinkedHashMap<>();
        RES_PARAMS.put("mchtTrdNo", hfDataModel.getMchtTrdNo());
        RES_PARAMS.put("trdNo", hfDataModel.getTrdNo());
        RES_PARAMS.put("vtlAcntNo", hfDataModel.getVtlAcntNo());
        RES_PARAMS.put("mchtCustId", hfDataModel.getMchtCustId());
        RES_PARAMS.put("fnNm", hfDataModel.getFnNm());
        RES_PARAMS.put("method", hfDataModel.getMethod());
        RES_PARAMS.put("trdAmt", hfDataModel.getTrdAmt());
        RES_PARAMS.put("outRsltMsg", hfDataModel.getOutRsltMsg());
        RES_PARAMS.put("mchtParam", hfDataModel.getMchtParam());
        RES_PARAMS.put("expireDt", hfDataModel.getExpireDt());
        RES_PARAMS.put("outStatCd", hfDataModel.getOutStatCd());
        RES_PARAMS.put("outRsltCd", hfDataModel.getOutRsltCd());
        RES_PARAMS.put("authDt", hfDataModel.getAuthDt());
        RES_PARAMS.put("mchtId", hfDataModel.getMchtId());
        RES_PARAMS.put("fnCd", hfDataModel.getFnCd());
        ObjectMapper objectMapper = new ObjectMapper();
        return Base64.getEncoder().encodeToString(objectMapper.writeValueAsString(RES_PARAMS).getBytes());
    }

    public String notiCAData(HFDataModel hfDataModel) {
        Map<String, String> RES_PARAMS = new LinkedHashMap<>();
        RES_PARAMS.put("outStatCd", hfDataModel.getOutStatCd());                    //거래상태
        RES_PARAMS.put("trdNo", hfDataModel.getTrdNo());                            //거래번호
        RES_PARAMS.put("method", hfDataModel.getMethod());                            //결제수단 (카드CA, 가상계좌VA)
        RES_PARAMS.put("bizType", hfDataModel.getBizType());                        //업무구분
        RES_PARAMS.put("mchtId", hfDataModel.getMchtId());                            //상점아이디 (헥토파이낸셜에서 부여하는 아이디)
        RES_PARAMS.put("mchtTrdNo", hfDataModel.getMchtTrdNo());                    //상점주문번호 (상점에서 생성하는 거래번호)
        RES_PARAMS.put("mchtCustNm", hfDataModel.getMchtCustNm());                    //주문자명 (실제 결제자의 주문자명)
        RES_PARAMS.put("mchtName", hfDataModel.getMchtName());                        //상점 한글명
        RES_PARAMS.put("pmtprdNm", hfDataModel.getPmtprdNm());                        //상품명
        RES_PARAMS.put("trdDtm", hfDataModel.getTrdDtm());                            //거래일시
        RES_PARAMS.put("trdAmt", hfDataModel.getTrdAmt());                            //거래금액
        RES_PARAMS.put("billKey", hfDataModel.getBillKey());                        //자동결제 키 (빌키 이용 상점만 해당)
        RES_PARAMS.put("billKeyExpireDt", hfDataModel.getBillKeyExpireDt());        //자동결제 키 유효기간
        RES_PARAMS.put("bankCd", hfDataModel.getBankCd());                            //은행코드
        RES_PARAMS.put("bankNm", hfDataModel.getBankNm());                            //은행명
        RES_PARAMS.put("cardCd", hfDataModel.getCardCd());                            //카드사 코드
        RES_PARAMS.put("cardNm", hfDataModel.getCardNm());                            //카드명
        RES_PARAMS.put("telecomCd", hfDataModel.getTelecomCd());                    //이통사코드
        RES_PARAMS.put("telecomNm", hfDataModel.getTelecomNm());                    //이통사명
        RES_PARAMS.put("vAcntNo", hfDataModel.getVAcntNo());                        //가상계좌번호
        RES_PARAMS.put("expireDt", hfDataModel.getExpireDt());                        //가상계좌 입금만료 일시
        RES_PARAMS.put("AcntPrintNm", hfDataModel.getAcntPrintNm());                //통장인자명 (고객통장에 찍힐 인자명)
        RES_PARAMS.put("dpstrNm", hfDataModel.getDpstrNm());                        //입금자명 (가상계좌에 실제 입금한 사람 이름)
        RES_PARAMS.put("email", hfDataModel.getEmail());                            //상점고객email
        RES_PARAMS.put("mchtCustId", hfDataModel.getMchtCustId());                    //상점고객아이디
        RES_PARAMS.put("cardNo", hfDataModel.getCardNo());                            //카드번호
        RES_PARAMS.put("cardApprNo", hfDataModel.getCardApprNo());                    //카드승인번호
        RES_PARAMS.put("instmtMon", hfDataModel.getInstmtMon());                    //할부개월수
        RES_PARAMS.put("instmtType", hfDataModel.getInstmtType());                    //할부타입
        RES_PARAMS.put("phoneNoEnc", hfDataModel.getPhoneNoEnc());                    //휴대폰번호암호화
        RES_PARAMS.put("orgTrdNo", hfDataModel.getOrgTrdNo());                        //원거래번호
        RES_PARAMS.put("orgTrdDt", hfDataModel.getOrgTrdDt());                        //원거래일자
        RES_PARAMS.put("mixTrdNo", hfDataModel.getMixTrdNo());                        //복합결제 거래번호
        RES_PARAMS.put("mixTrdAmt", hfDataModel.getMixTrdAmt());                    //복합결제 금액
        RES_PARAMS.put("payAmt", hfDataModel.getPayAmt());                            //실 결제금액 (복합결제 금액을 제외한 결제금액)
        RES_PARAMS.put("csrcIssNo", hfDataModel.getCsrcIssNo());                    //현금영수증 승인번호
        RES_PARAMS.put("cnclType", hfDataModel.getCnclType());                        //취소거래 타입
        RES_PARAMS.put("mchtParam", hfDataModel.getMchtParam());                    //상점예약필드  (추가 정보 필드)
        RES_PARAMS.put("pktHash", hfDataModel.getPktHash());                        //해쉬값

        /** 해쉬 조합 필드
         *  결과코드 + 거래일시 + 상점아이디 + 가맹점거래번호 + 거래금액 + 라이센스키 */
        String hashPlain = new HFDataModel(
                hfDataModel.getMchtTrdNo(),
                hfDataModel.getTrdAmt(),
                hfDataModel.getOutStatCd(),
                hfDataModel.getMchtId(),
                hfDataModel.getTrdDtm()
        ).getHashPlain() + constant.LICENSE_KEY;
        String hashCipher = "";

        /** SHA256 해쉬 처리 */
        try {
            hashCipher = EncryptUtil.digestSHA256(hashPlain);//해쉬 값
        } catch (Exception e) {
            logger.error("[" + RES_PARAMS.get("mchtTrdNo") + "][SHA256 HASHING] Hashing Fail! : " + e.toString());
        } finally {
            logger.info("[" + RES_PARAMS.get("mchtTrdNo") + "][SHA256 HASHING] Plain Text[" + hashPlain + "] ---> Cipher Text[" + hashCipher + "]");
        }

        return responseMessage(hashCipher, RES_PARAMS.get("mchtTrdNo"), RES_PARAMS.get("pktHash"), RES_PARAMS.get("outStatCd"), RES_PARAMS);
    }


    public String notiVAData(HFDataModel hfDataModel) {
        Map<String, String> RES_PARAMS = new LinkedHashMap<>();
        RES_PARAMS.put("outStatCd", hfDataModel.getOutStatCd());        //거래상태
        RES_PARAMS.put("trdNo", hfDataModel.getTrdNo());                //거래번호
        RES_PARAMS.put("method", hfDataModel.getMethod());                //결제수단 (카드CA, 가상계좌VA)
        RES_PARAMS.put("bizType", hfDataModel.getBizType());            //업무구분	(???)
        RES_PARAMS.put("mchtId", hfDataModel.getMchtId());                //상점아이디 (헥토파이낸셜에서 부여하는 아이디)
        RES_PARAMS.put("mchtTrdNo", hfDataModel.getMchtTrdNo());        //상점주문번호 (상점에서 생성하는 거래번호)
        RES_PARAMS.put("mchtCustNm", hfDataModel.getMchtCustNm());        //고객명
        RES_PARAMS.put("mchtName", hfDataModel.getMchtName());            //상점한글명
        RES_PARAMS.put("pmtprdNm", hfDataModel.getPmtprdNm());            //상품명
        RES_PARAMS.put("trdDtm", hfDataModel.getTrdDtm());                //거래일시
        RES_PARAMS.put("trdAmt", hfDataModel.getTrdAmt());                //거래금액
        RES_PARAMS.put("bankCd", hfDataModel.getBankCd());                //은행코드 (금융기관 식별자 011, ...)
        RES_PARAMS.put("bankNm", hfDataModel.getBankNm());                //은행명 (NH 농협, 신한)
        RES_PARAMS.put("acntType", hfDataModel.getAcntType());            //계좌구분 ("1 : 기본(회전식), 2 : 고정식 ..." )
        RES_PARAMS.put("vAcntNo", hfDataModel.getVAcntNo());            //가상계좌번호
        RES_PARAMS.put("expireDt", hfDataModel.getExpireDt());            //가상계좌 입금만료 일시
        RES_PARAMS.put("AcntPrintNm", hfDataModel.getAcntPrintNm());    //통장인자명 (고객통장에 찍힐 인자명)
        RES_PARAMS.put("dpstrNm", hfDataModel.getDpstrNm());            //입금자명 (가상계좌에 실제 입금한 사람 이름)
        RES_PARAMS.put("email", hfDataModel.getEmail());                //상점고객email
        RES_PARAMS.put("mchtCustId", hfDataModel.getMchtCustId());        //상점고객아이디
        RES_PARAMS.put("orgTrdNo", hfDataModel.getOrgTrdNo());            //원거래번호
        RES_PARAMS.put("orgTrdDt", hfDataModel.getOrgTrdDt());            //원거래일자
        RES_PARAMS.put("csrcIssNo", hfDataModel.getCsrcIssNo());        //현금영수증 승인번호
        RES_PARAMS.put("cnclType", hfDataModel.getCnclType());            //취소거래타입
        RES_PARAMS.put("mchtParam", hfDataModel.getMchtParam());        //상점예약필드 (추가 정보 필드)
        RES_PARAMS.put("pktHash", hfDataModel.getPktHash());            //해쉬값

        /** 해쉬 조합 필드
         *  결과코드 + 거래일시 + 상점아이디 + 가맹점거래번호 + 거래금액 + 라이센스키 */
        String hashPlain = new HFDataModel(
                hfDataModel.getMchtTrdNo(),
                hfDataModel.getTrdAmt(),
                hfDataModel.getOutStatCd(),
                hfDataModel.getMchtId(),
                hfDataModel.getTrdDtm()
        ).getHashPlain() + constant.LICENSE_KEY;
        String hashCipher = "";

        /** SHA256 해쉬 처리 */
        try {
            hashCipher = EncryptUtil.digestSHA256(hashPlain);//해쉬 값
        } catch (Exception e) {
            logger.error("[" + RES_PARAMS.get("mchtTrdNo") + "][SHA256 HASHING] Hashing Fail! : " + e.toString());
        } finally {
            logger.info("[" + RES_PARAMS.get("mchtTrdNo") + "][SHA256 HASHING] Plain Text[" + hashPlain + "] ---> Cipher Text[" + hashCipher + "]");
        }

        return responseMessage(hashCipher, RES_PARAMS.get("mchtTrdNo"), RES_PARAMS.get("pktHash"), RES_PARAMS.get("outStatCd"), RES_PARAMS);
    }


    private String responseMessage(String hashCipher, String mchtTrdNo, String pktHash, String outStatCd, Map<String, String> responseParam) {
        boolean resp = false;
        /**
         hash데이타값이 맞는 지 확인 하는 루틴은 헥토파이낸셜에서 받은 데이타가 맞는지 확인하는 것이므로 꼭 사용하셔야 합니다
         정상적인 결제 건임에도 불구하고 노티 페이지의 오류나 네트웍 문제 등으로 인한 hash 값의 오류가 발생할 수도 있습니다.
         그러므로 hash 오류건에 대해서는 오류 발생시 원인을 파악하여 즉시 수정 및 대처해 주셔야 합니다.
         그리고 정상적으로 데이터를 처리한 경우에도 헥토파이낸셜에서 응답을 받지 못한 경우는 결제결과가 중복해서 나갈 수 있으므로 관련한 처리도 고려되어야 합니다
         */
        if (hashCipher.equals(pktHash)) {
            logger.info("[" + mchtTrdNo + "][SHA256 Hash Check] hashCipher[" + hashCipher + "] pktHash[" + pktHash + "] equals?[TRUE]");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Calendar cal = Calendar.getInstance();
            Date regDate = cal.getTime();
            Date modDate = cal.getTime();

            String[] pairs = responseParam.get("mchtParam").split("&");

            Map<String, String> parseParams = parseParams(pairs);

            try {
                String agencyId = parseParams.get("agencyId");
                String siteId = parseParams.get("siteId");
                Date startDate = sdf.parse(parseParams.get("startDate"));
                Date endDate = sdf.parse(parseParams.get("endDate"));
                String rateSel = parseParams.get("rateSel");
                String offer = parseParams.get("offer");
                Optional<AgencyInfoKey> agencyInfoKey = loadEncryptDataPort.getAgencyInfoKey(agencyId);
                String targetUrl = "";


                //해당 거래번호
                Optional<PaymentHistory> checkHistory = loadPaymentDataPort.getPaymentHistoryByTradeNum(responseParam.get("trdNo"));
                if ("0021".equals(outStatCd)) {
                    System.out.println("outStatCd equals 0021");
                    switch (responseParam.get("method")) {
                        case "CA": {
                            if (checkHistory.isEmpty() && responseParam.get("bizType").equals("B0")) {
                                resp = true;
                                String billKey = responseParam.get("billKey");
                                PaymentHistory paymentHistory;
                                if (billKey != null && !billKey.isEmpty()) {
                                    paymentHistory = createAutoPaymentHistory(responseParam, agencyId, siteId, rateSel, offer, startDate, endDate, regDate);
                                } else {
                                    paymentHistory = createDefaultPaymentHistory(responseParam, agencyId, siteId, rateSel, offer, startDate, endDate, regDate);
                                }

                                System.out.println("0021 card paymentHistory : " + paymentHistory);
                                // Prepare JSON data for notifications
                                Map<String, String> jsonData = prepareJsonDataForNotification(agencyId, siteId, responseParam.get("mchtTrdNo"));
                                Map<String, String> notifyPaymentData = prepareNotifyPaymentData(agencyId, siteId, startDate, endDate, rateSel, responseParam.get("trdAmt"));

                                savePaymentDataPort.insertPayment(paymentHistory);
                                //결제완료 후 Agency 상태 업데이트 (시작일, 종료일, 상품코드, 연장가능여부 N)
                                saveAgencyDataPort.updateAgency(new Agency(agencyId, siteId), new Client(rateSel, startDate, endDate), EnumPaymentStatus.ACTIVE.getCode());

                                updateExtraAmountStatus(agencyId, siteId, responseParam.get("method"));

                                targetUrl = makeTargetUrl(agencyInfoKey, agencyId);

                                //AdminNoti
                                System.out.println("어드민 결제 노티 완료 targetUrl : " + profileSpecificAdminUrl + ", Data : " + encryptUseCase.mapToJSONString(jsonData));
                                notiUseCase.sendNotification(profileSpecificAdminUrl + "/clientManagement/agency/payment/noti", encryptUseCase.mapToJSONString(jsonData));

                                //가맹점Noti
                                System.out.println("가맹점 결제 노티 완료 targetUrl : " + targetUrl + ", Data : " + makeAgencyNotifyData(agencyId, notifyPaymentData));
                                notiUseCase.sendNotification(targetUrl, makeAgencyNotifyData(agencyId, notifyPaymentData));
                                break;
                            }
                        }
                        case "VA": {
                            resp = true;
                            PaymentHistory paymentHistory = createVirtualAccountPaymentHistory(responseParam, agencyId, siteId, rateSel, offer, EnumTradeTrace.USED.getCode(), EnumPaymentStatus.ACTIVE.getCode(), startDate, endDate, regDate, modDate);
                            System.out.println("0021 vBank paymentHistory : " + paymentHistory);
                            // Prepare JSON data for notifications
                            Map<String, String> jsonData = prepareJsonDataForNotification(agencyId, siteId, responseParam.get("mchtTrdNo"));
                            Map<String, String> notifyPaymentData = prepareNotifyPaymentData(agencyId, siteId, startDate, endDate, rateSel, responseParam.get("trdAmt"));


                            savePaymentDataPort.updatePayment(paymentHistory);
                            //결제완료 후 Agency 상태 업데이트 (시작일, 종료일, 상품코드, 연장가능여부 N)
                            saveAgencyDataPort.updateAgency(new Agency(agencyId, siteId), new Client(rateSel, startDate, endDate), EnumPaymentStatus.ACTIVE.getCode());

                            updateExtraAmountStatus(agencyId, siteId, responseParam.get("method"));

                            targetUrl = makeTargetUrl(agencyInfoKey, agencyId);

                            //AdminNoti
                            System.out.println("어드민 결제 노티 완료 targetUrl : " + profileSpecificAdminUrl + ", Data : " + encryptUseCase.mapToJSONString(jsonData));
                            notiUseCase.sendNotification(profileSpecificAdminUrl + "/clientManagement/agency/payment/noti", encryptUseCase.mapToJSONString(jsonData));
                            //가맹점Noti
                            System.out.println("가맹점 노티 완료 targetUrl : " + targetUrl + ", Data : " + makeAgencyNotifyData(agencyId, notifyPaymentData));
                            notiUseCase.sendNotification(targetUrl, makeAgencyNotifyData(agencyId, notifyPaymentData));
                            break;
                        }
                    }
                } else if ("0051".equals(outStatCd)) {
                    if (checkHistory.isEmpty()) {
                        PaymentHistory paymentHistory = createVirtualAccountPaymentHistory(responseParam, agencyId, siteId, rateSel, offer, EnumTradeTrace.NOT_USED.getCode(), EnumPaymentStatus.NOT_DEPOSITED.getCode(), startDate, endDate, regDate, null);
                        System.out.println("0051 vBank paymentHistory : " + paymentHistory);
                        savePaymentDataPort.insertPayment(paymentHistory);
                        //결제완료 후 Agency 상태 업데이트 (시작일, 종료일, 상품코드, 연장가능여부 N)
                        saveAgencyDataPort.updateAgency(new Agency(agencyId, siteId), new Client(rateSel, startDate, endDate), EnumPaymentStatus.NOT_DEPOSITED.getCode());

                        String plainData = makePlainDataData(agencyId, siteId, paymentHistory);
                        String encryptData = encryptUseCase.encryptData(agencyId, plainData);
                        String verifyInfo = encryptUseCase.hmacSHA256(plainData, agencyId);
                        String msgType = "NotifyStatusSite";

                        Map<String, String> notifyStatusSiteDataByVbank = new HashMap<>();
                        notifyStatusSiteDataByVbank.put("agencyId", agencyId);
                        notifyStatusSiteDataByVbank.put("msgType", msgType);
                        notifyStatusSiteDataByVbank.put("encryptData", encryptData);
                        notifyStatusSiteDataByVbank.put("verifyInfo", verifyInfo);
                        String requestStatusSiteData = encryptUseCase.mapToJSONString(notifyStatusSiteDataByVbank);
                        notiUseCase.getAgencyUrlByAgencyInfoKey(agencyId, msgType);

                        targetUrl = makeTargetUrl(agencyInfoKey, agencyId);
                        System.out.println("가맹점 노티 완료 targetUrl : " + targetUrl + ", Data : " + requestStatusSiteData);
                        notiUseCase.sendNotification(targetUrl, requestStatusSiteData);
                    }
                    resp = true;
                } else {
                    resp = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            logger.info("[" + mchtTrdNo + "][SHA256 Hash Check] hashCipher[" + hashCipher + "] pktHash[" + pktHash + "] equals?[FALSE]");
            // resp = notiHashError(noti); // 실패 처리
        }
        // OK, FAIL문자열은 헥토파이낸셜로 전송되어야 하는 값이므로 변경하거나 삭제하지마십시오.
        if (resp) {
            logger.info("[" + mchtTrdNo + "][Result] OK");
            return "OK";
        } else {
            logger.info("[" + mchtTrdNo + "][Result] FAIL");
            return "FAIL";
        }
    }


    public Map<String, String> decryptData(HFResultDataModel resultDataModel) {
        /** 응답 파라미터 세팅 */
        Map<String, String> responseParams = new LinkedHashMap<>();
        /** 설정 정보 저장 */
        String aesKey = constant.AES256_KEY;

        responseParams.put("mchtId", resultDataModel.getMchtId());            //상점아이디
        responseParams.put("outStatCd", resultDataModel.getOutStatCd());         //결과코드
        responseParams.put("outRsltCd", resultDataModel.getOutRsltCd());         //거절코드
        responseParams.put("outRsltMsg", resultDataModel.getOutRsltMsg());        //결과메세지
        responseParams.put("method", resultDataModel.getMethod());            //결제수단
        responseParams.put("mchtTrdNo", resultDataModel.getMchtTrdNo());         //상점주문번호
        responseParams.put("mchtCustId", resultDataModel.getMchtCustId());        //상점고객아이디
        responseParams.put("trdNo", resultDataModel.getTrdNo());             //세틀뱅크 거래번호
        responseParams.put("trdAmt", resultDataModel.getTrdAmt());            //거래금액
        responseParams.put("mchtParam", resultDataModel.getMchtParam());         //상점 예약필드
        responseParams.put("authDt", resultDataModel.getAuthDt());            //승인일시
        responseParams.put("authNo", resultDataModel.getAuthNo());            //승인번호
        responseParams.put("reqIssueDt", resultDataModel.getReqIssueDt());       //채번요청일시
        responseParams.put("intMon", resultDataModel.getIntMon());            //할부개월수
        responseParams.put("fnNm", resultDataModel.getFnNm());              //카드사명
        responseParams.put("fnCd", resultDataModel.getFnCd());              //카드사코드
        responseParams.put("pointTrdNo", resultDataModel.getPointTrdNo());        //포인트거래번호
        responseParams.put("pointTrdAmt", resultDataModel.getPointTrdAmt());       //포인트거래금액
        responseParams.put("cardTrdAmt", resultDataModel.getCardTrdAmt());        //신용카드결제금액
        responseParams.put("vtlAcntNo", resultDataModel.getVtlAcntNo());         //가상계좌번호
        responseParams.put("expireDt", resultDataModel.getExpireDt());          //입금기한
        responseParams.put("cphoneNo", resultDataModel.getCphoneNo());          //휴대폰번호
        responseParams.put("billKey", resultDataModel.getBillKey());           //자동결제키
        responseParams.put("csrcAmt", resultDataModel.getCsrcAmt());           //현금영수증 발급 금액(네이버페이)

        //AES256 복호화 필요 파라미터
//        String[] DECRYPT_PARAMS = {"mchtCustId", "trdAmt", "pointTrdAmt", "cardTrdAmt", "vtlAcntNo", "cphoneNo", "csrcAmt"};
        String[] DECRYPT_PARAMS = {"mchtCustId", "trdAmt", "vtlAcntNo"};


        decryptParams(DECRYPT_PARAMS, responseParams, responseParams);

        System.out.println("responseParams " + responseParams);
        return responseParams;
    }

    public String hashPkt(Map<String, String> requestMap) {
        String hashPlain = "";
        String hashCipher = "";
        try {
            hashPlain = requestMap.get("trdDt")
                    + requestMap.get("trdTm")
                    + requestMap.get("mchtId")
                    + requestMap.get("mchtTrdNo")
                    + requestMap.get("trdAmt")
                    + constant.LICENSE_KEY;
            hashCipher = EncryptUtil.digestSHA256(hashPlain);
        } catch (Exception e) {
            logger.error("[" + requestMap.get("mchtTrdNo") + "][SHA256 HASHING] Hashing Fail! : " + e.toString());
        } finally {
            logger.info("[" + requestMap.get("mchtTrdNo") + "][SHA256 HASHING] Plain Text[" + hashPlain + "] ---> Cipher Text[" + hashCipher + "]");
        }
        return hashCipher; //해쉬 결과 값 세팅
    }

    public void encryptParams(String[] ENCRYPT_PARAMS, Map<String, String> REQ_HEADER, Map<String, String> REQ_BODY) {
        try {
            for (int i = 0; i < ENCRYPT_PARAMS.length; i++) {
                String aesPlain = REQ_BODY.get(ENCRYPT_PARAMS[i]);
                if ((aesPlain != null) && !aesPlain.isEmpty()) {
                    byte[] aesCipherRaw = EncryptUtil.aes256EncryptEcb(constant.AES256_KEY, aesPlain);
                    String aesCipher = EncryptUtil.encodeBase64(aesCipherRaw);

                    REQ_BODY.put(ENCRYPT_PARAMS[i], aesCipher); //암호화 결과 값 세팅
                    logger.info("[" + REQ_HEADER.get("mchtTrdNo") + "][AES256 Encrypt] " + ENCRYPT_PARAMS[i] + "[" + aesPlain + "] ---> [" + aesCipher + "]");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[" + REQ_HEADER.get("mchtTrdNo") + "][AES256 Encrypt] AES256 Encrypt Fail! : " + e.toString());
        }
    }

    public void decryptParams(String[] DECRYPT_PARAMS, Map<String, String> REQ_HEADER, Map<String, String> respParam) {
        try {
            for (int i = 0; i < DECRYPT_PARAMS.length; i++) {
                if (respParam.containsKey(DECRYPT_PARAMS[i])) {
                    String aesCipher = (respParam.get(DECRYPT_PARAMS[i])).trim();
                    if (!("".equals(aesCipher))) {
                        System.out.println("for each aesCipher : " + aesCipher);
                        byte[] aesCipherRaw = EncryptUtil.decodeBase64(aesCipher);
                        String aesPlain = new String(EncryptUtil.aes256DecryptEcb(constant.AES256_KEY, aesCipherRaw), "UTF-8");

                        respParam.put(DECRYPT_PARAMS[i], aesPlain);//복호화된 데이터로 세팅
                        logger.info("[" + REQ_HEADER.get("mchtTrdNo") + "][AES256 Decrypt] " + DECRYPT_PARAMS[i] + "[" + aesCipher + "] ---> [" + aesPlain + "]");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("[" + REQ_HEADER.get("mchtTrdNo") + "][AES256 Decrypt] AES256 Decrypt Fail! : " + e.toString());
        }
    }


    private PaymentHistory createAutoPaymentHistory(Map<String, String> responseParam, String agencyId, String siteId, String rateSel, String offer, Date startDate, Date endDate, Date regDate) throws ParseException {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return new PaymentHistory(
                responseParam.get("mchtTrdNo"),
                responseParam.get("trdNo"),
                agencyId,
                siteId,
                responseParam.get("method"),
                rateSel,
                responseParam.get("trdAmt"),
                offer,
                EnumTradeTrace.USED.getCode(),
                EnumPaymentStatus.ACTIVE.getCode(),
                originalFormat.parse(responseParam.get("trdDtm")),
                startDate,
                endDate,
                responseParam.get("billKey"),
                responseParam.get("billKeyExpireDt"),
                regDate,
                EnumExtraAmountStatus.PASS.getCode()
        );
    }

    private PaymentHistory createDefaultPaymentHistory(Map<String, String> responseParam, String agencyId, String siteId, String rateSel, String offer, Date startDate, Date endDate, Date regDate) throws ParseException {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return new PaymentHistory(
                responseParam.get("mchtTrdNo"),
                responseParam.get("trdNo"),
                agencyId,
                siteId,
                responseParam.get("method"),
                rateSel,
                responseParam.get("trdAmt"),
                offer,
                EnumTradeTrace.USED.getCode(),
                EnumPaymentStatus.ACTIVE.getCode(),
                originalFormat.parse(responseParam.get("trdDtm")),
                startDate,
                endDate,
                regDate,
                EnumExtraAmountStatus.PASS.getCode()
        );
    }

    private PaymentHistory createVirtualAccountPaymentHistory(Map<String, String> responseParam, String agencyId, String siteId, String rateSel, String offer, String tradeTrace, String paymentStatus, Date startDate, Date endDate, Date regDate, Date modDate) throws ParseException {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return new PaymentHistory(
                responseParam.get("mchtTrdNo"),
                responseParam.get("trdNo"),
                agencyId,
                siteId,
                responseParam.get("method"),
                rateSel,
                responseParam.get("trdAmt"),
                offer,
                tradeTrace,
                paymentStatus,
                originalFormat.parse(responseParam.get("trdDtm")),
                responseParam.get("AcntPrintNm"),
                responseParam.get("bankNm"),
                responseParam.get("bankCd"),
                responseParam.get("vAcntNo"),
                originalFormat.parse(responseParam.get("expireDt")),
                startDate,
                endDate,
                regDate,
                modDate,
                EnumExtraAmountStatus.PASS.getCode()
        );
    }

    private String makeTargetUrl(Optional<AgencyInfoKey> agencyInfoKey, String agencyId) throws JsonProcessingException {
        String msgType = "";
        if (agencyInfoKey.isPresent()) {
            AgencyInfoKey info = agencyInfoKey.get();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> agencyUrlJson = mapper.readValue(info.getAgencyUrl(), new TypeReference<>() {
            });
            EnumAgency[] enumAgencies = EnumAgency.values();
            for (EnumAgency enumAgency : enumAgencies) {
                if (enumAgency.getCode().equals(agencyId)) {
                    msgType = enumAgency.getPaymentMsg();
                    break;
                }
            }
            return agencyUrlJson.get(msgType);
        }
        return "";
    }


    private Map<String, String> prepareJsonDataForNotification(String agencyId, String siteId, String trdNum) {
        Map<String, String> jsonData = new HashMap<>();
        jsonData.put("agencyId", agencyId);
        jsonData.put("siteId", siteId);
        jsonData.put("tradeNum", trdNum);
        return jsonData;
    }

    private Map<String, String> prepareNotifyPaymentData(String agencyId, String siteId, Date startDate, Date endDate, String rateSel, String salesPrice) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, String> notifyPaymentData = new HashMap<>();
        notifyPaymentData.put("agencyId", agencyId);
        notifyPaymentData.put("siteId", siteId);
        notifyPaymentData.put("startDate", sdf.format(startDate));
        notifyPaymentData.put("endDate", sdf.format(endDate));
        notifyPaymentData.put("rateSel", rateSel);
        notifyPaymentData.put("salesPrice", salesPrice);
        return notifyPaymentData;
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

    private String makeAgencyNotifyData(String agencyId, Map<String, String> notifyPaymentData) throws GeneralSecurityException {
        JSONObject json = new JSONObject();
        json.put("agencyId", agencyId);
        EnumAgency[] enumAgencies = EnumAgency.values();
        for (EnumAgency enumAgency : enumAgencies) {
            if (enumAgency.getCode().equals(agencyId)) {
                json.put("msgType", enumAgency.getPaymentMsg());
                break;
            }
        }
        json.put("encryptData", encryptUseCase.encryptData(agencyId, encryptUseCase.mapToJSONString(notifyPaymentData)));
        json.put("verifyInfo", encryptUseCase.hmacSHA256(encryptUseCase.mapToJSONString(notifyPaymentData), agencyId));

        return json.toString();
    }


    private String makePlainDataData(String agencyId, String siteId, PaymentHistory paymentHistory) {
        String custName = paymentHistory.getRcptName();
        String accountNumber = paymentHistory.getVbankAccount();
        String bankName = paymentHistory.getVbankName();
        String siteStatus = "V";
        String detail = "{\"custName\" :" + custName + "," +
                " \"accountNumber\":" + accountNumber + "," +
                "\"bankName:\":" + bankName + "}";

        Map<String, String> plainDataMap = new HashMap<>();
        plainDataMap.put("agencyId", agencyId);
        plainDataMap.put("siteId", siteId);
        plainDataMap.put("siteStatus", siteStatus);
        plainDataMap.put("detail", detail);
        return encryptUseCase.mapToJSONString(plainDataMap);
    }


    private void updateExtraAmountStatus(String agencyId, String siteId, String paymentType) {
        List<PaymentHistory> paymentHistoryList = loadPaymentDataPort.getPaymentHistoryByAgency(new Agency(agencyId, siteId))
                .stream()
                .filter(e -> e.getExtraAmountStatus().equals(EnumExtraAmountStatus.PASS.getCode()))
                .collect(Collectors.toList());
        if (!paymentHistoryList.isEmpty() && paymentHistoryList.size() > 2) {
            if (paymentHistoryList.get(2).getExtraAmountStatus().equals(EnumExtraAmountStatus.PASS.getCode())){
                savePaymentDataPort.updatePaymentExtraAmountStatus(paymentHistoryList.get(2));
            }
        }
    }




}




