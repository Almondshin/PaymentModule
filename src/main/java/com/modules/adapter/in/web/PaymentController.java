package com.modules.adapter.in.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modules.adapter.in.models.ClientDataContainer;
import com.modules.adapter.in.models.PaymentHistoryDataModel;
import com.modules.adapter.out.payment.config.hectofinancial.Constant;
import com.modules.adapter.out.payment.utils.EncryptUtil;
import com.modules.adapter.out.payment.utils.HttpClientUtil;
import com.modules.application.domain.AgencyInfoKey;
import com.modules.application.domain.AgencyProducts;
import com.modules.application.enums.*;
import com.modules.application.exceptions.enums.EnumResultCode;
import com.modules.application.port.in.AgencyUseCase;
import com.modules.application.port.in.NotiUseCase;
import com.modules.application.port.in.PaymentUseCase;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Payment controller.
 */
@Slf4j
@RestController
@RequestMapping(value = {"/agency/payment", "/payment"})
public class PaymentController {

    private final AgencyUseCase agencyUseCase;
    private final Constant constant;
    private final PaymentUseCase paymentUseCase;
    private final NotiUseCase notiUseCase;

    @Value("${external.url}")
    private String profileSpecificUrl;

    @Value("${external.payment.url}")
    private String profileSpecificPaymentUrl;

    /**
     * The Logger.
     */
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Instantiates a new Payment controller.
     *
     * @param agencyUseCase  the agency use case
     * @param constant       the constant
     * @param paymentUseCase the payment use case
     */
    public PaymentController(AgencyUseCase agencyUseCase, Constant constant, PaymentUseCase paymentUseCase, NotiUseCase notiUseCase) {
        this.agencyUseCase = agencyUseCase;
        this.constant = constant;
        this.paymentUseCase = paymentUseCase;
        this.notiUseCase = notiUseCase;
    }


    /**
     * 결제 정보 요청
     *
     * @param clientDataContainer 필수 값 : AgencyId, SiteId , 옵션 값 : RateSel, StartDate
     * @return resultCode, resultMsg, siteId, RateSel list,
     */
    @PostMapping("/getPaymentInfo")
    public ResponseEntity<?> getPaymentInfo(@RequestBody ClientDataContainer clientDataContainer) {
        Optional<ClientDataContainer> optClientInfo = agencyUseCase.getAgencyInfo(new ClientDataContainer(clientDataContainer));
        List<Map<String, String>> productTypes = agencyUseCase.getProductTypes(clientDataContainer.getKeyString(clientDataContainer));
        Map<String, Object> responseMessage = new HashMap<>();

        if (optClientInfo.isPresent()) {
            ClientDataContainer clientInfo = optClientInfo.get();

            String rateSel = clientDataContainer.determineRateSelection(clientInfo, clientDataContainer);
            String startDate = clientDataContainer.decideStartDate(clientInfo, clientDataContainer);

            ResponseEntity<?> siteStatusResponse = decideSiteStatus(responseMessage, clientInfo);
            if (siteStatusResponse != null) {
                return siteStatusResponse;
            }
            ResponseEntity<?> siteScheduledRateSelResponse = checkedScheduledRateSel(responseMessage, clientInfo);
            if (siteScheduledRateSelResponse != null) {
                return siteScheduledRateSelResponse;
            }

            Map<String, String> extendableAgencyId = clientDataContainer.checkedExtensionStatus(clientInfo);

            if (!extendableAgencyId.isEmpty()) {
                Map<String, Integer> excessMap = paymentUseCase.getExcessAmount(
                        paymentUseCase.getPaymentHistoryByAgency(extendableAgencyId.get("agencyId"), extendableAgencyId.get("siteId"))
                );
                responseMessage.put("extensionStatus", EnumExtensionStatus.EXTENDABLE.getCode());
                responseMessage.put("excessCount", excessMap.get("excessCount"));
                responseMessage.put("excessAmount", excessMap.get("excessAmount"));
            }

            Map<String, String> clientInfoMap = clientDataContainer.makeClientInfoMap(clientInfo);
            responseMessage.put("clientInfo", clientInfoMap.get("companyName") + "," + clientInfoMap.get("bizNumber") + "," + clientInfoMap.get("ceoName"));
            responseMessage.put("rateSel", rateSel);
            responseMessage.put("startDate", startDate);
        }

        responseMessage.put("resultCode", EnumResultCode.SUCCESS.getCode());
        responseMessage.put("resultMsg", EnumResultCode.SUCCESS.getValue());
        responseMessage.put("profileUrl", profileSpecificUrl);
        responseMessage.put("profilePaymentUrl", profileSpecificPaymentUrl);
        responseMessage.put("siteId", clientDataContainer.concatenateSiteId(clientDataContainer));
        responseMessage.put("listSel", productTypes);

        return ResponseEntity.ok(responseMessage);
    }


    /**
     * Sets payment site info.
     *
     * @param clientDataContainer the client data model
     * @return the payment site info
     */
    @PostMapping("/setPaymentSiteInfo")
    public ResponseEntity<?> setPaymentSiteInfo(@RequestBody ClientDataContainer clientDataContainer) {
        Map<String, Object> responseMessage = new HashMap<>();
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");
        String tradeNum = paymentUseCase.makeTradeNum();

        try {
            paymentUseCase.checkMchtParams(clientDataContainer);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String trdDt = ldt.format(dateFormatter);
        String trdTm = ldt.format(timeFormatter);
        String merchantId = clientDataContainer.getMerchantIdBasedOnMethod(clientDataContainer, constant);

        responseMessage.put("resultCode", EnumResultCode.SUCCESS.getCode());
        responseMessage.put("resultMsg", EnumResultCode.SUCCESS.getValue());
        responseMessage.put("mchtId", merchantId);
        responseMessage.put("method", clientDataContainer.getClientMethod(clientDataContainer));
        responseMessage.put("trdDt", trdDt);
        responseMessage.put("trdTm", trdTm);
        responseMessage.put("mchtTrdNo", tradeNum);
        responseMessage.put("trdAmt", clientDataContainer.getClientSalesPrice(clientDataContainer));
        responseMessage.put("hashCipher", paymentUseCase.aes256EncryptEcb(clientDataContainer, tradeNum, trdDt, trdTm));
        responseMessage.put("encParams", paymentUseCase.encodeBase64(clientDataContainer, tradeNum));

        return ResponseEntity.ok(responseMessage);
    }

    @PostMapping(value = "/cardCancel")
    public void requestCardCancelPayment(@RequestBody String requestSiteId) throws JsonProcessingException {
        Map<String, Object> requestData = new HashMap<>();
        Map<String, String> params = new HashMap<>();

        String ver = "0A19"; // 전문의 버전 "0A19"고정값
        String method = "CA"; // 결제 수단 "CA" 고정값
        String bizType = "C0"; // 업무 구분코드 "C0" 고정값
        String encCd = "23"; //  암호화 구분 코드 "23" 고정값
        String mchtId = constant.getPG_CANCEL_MID_CARD(); // 상점아이디
        String mchtTrdNo = "GYCNCL" + UUID.randomUUID().toString().replace("-", "");
        LocalDateTime now = LocalDateTime.now();
        String trdDt = now.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String trdTm = now.toLocalTime().format(DateTimeFormatter.ofPattern("HHmmss"));

        params.put("mchtId", mchtId); // 헥토파이낸셜 부여 상점 아이디
        params.put("ver", ver); // 버전 (고정값)
        params.put("method", method); // 결제수단 (고정값)
        params.put("bizType", bizType); // 업무 구분 코드 (고정값)
        params.put("encCd", encCd);   // 암호화 구분 코드 (고정값)
        params.put("mchtTrdNo", mchtTrdNo); // 상점 주문번호
        params.put("trdDt", trdDt); // 취소 요청 일자
        params.put("trdTm", trdTm);   // 취소 요청 시간


        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> mapData = mapper.readValue(requestSiteId, Map.class);

        String agencyId = mapData.get("agencyId");
        String siteId = mapData.get("siteId").split("-")[1];

        Optional<PaymentHistoryDataModel> optPaymentHistory = paymentUseCase.getPaymentHistoryByAgency(agencyId, siteId)
                .stream()
                .filter(e -> e.getTradeNum().equals(mapData.get("tradeNum")))
                .findFirst();

        if (optPaymentHistory.isPresent()) {
            PaymentHistoryDataModel paymentHistoryDataModel = optPaymentHistory.get();
            Map<String, String> data = new HashMap<>();

            if (paymentHistoryDataModel.getRateSel().toLowerCase().contains("autopay")) {
                mchtId = constant.getPG_CANCEL_MID_AUTO();
                params.put("mchtId", mchtId); // 헥토파이낸셜 부여 상점 아이디
            }

            data.put("orgTrdNo", paymentHistoryDataModel.getPgTradeNum()); // 원거래번호 : 결제시 헥토에서 발급한 거래번호
            data.put("crcCd", "KRW"); // 통화구분 "KRW" 고정값
            data.put("cnclOrd", "001"); // 취소 회차
            data.put("cnclAmt", paymentHistoryDataModel.getAmount()); // 취소 금액 AES 암호화

            try {
                // 취소요청일자 + 취소요청시간 + 상점아이디 + 상점주문번호 + 취소금액(평문) + 해쉬키
                data.put("pktHash", EncryptUtil.digestSHA256(
                        trdDt + trdTm + mchtId + mchtTrdNo + paymentHistoryDataModel.getAmount() + constant.LICENSE_KEY)
                );
                data.put("cnclAmt", Base64.getEncoder().encodeToString(EncryptUtil.aes256EncryptEcb(constant.AES256_KEY, data.get("cnclAmt"))));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            requestData.put("params", params);
            requestData.put("data", data);
            String url = constant.BILL_SERVER_URL + "/spay/APICancel.do";

            HttpClientUtil httpClientUtil = new HttpClientUtil();
            String resData = httpClientUtil.sendApi(url, requestData, 5000, 25000);
            System.out.println(resData);

            String hashCipher = "";
            String hashPlain = trdDt + trdTm + mchtId + mchtTrdNo + paymentHistoryDataModel.getAmount() + constant.LICENSE_KEY;

            /** SHA256 해쉬 처리 */
            try {
                hashCipher = EncryptUtil.digestSHA256(hashPlain);//해쉬 값
            } catch (Exception e) {
                logger.error("[" + params.get("mchtTrdNo") + "][SHA256 HASHING] Hashing Fail! : " + e.toString());
            } finally {
                logger.info("[" + params.get("mchtTrdNo") + "][SHA256 HASHING] Plain Text[" + hashPlain + "] ---> Cipher Text[" + hashCipher + "]");
            }
        }
    }


    /**
     * Request bill key payment.
     *
     * @param requestMsg the request map
     */
    @PostMapping(value = "/bill")
    public void requestBillKeyPayment(@RequestBody String requestMsg) {
        Map<String, Object> responseData = new HashMap<>();
        Map<String, String> params = new HashMap<>();

        String ver = "0A19";
        String method = "CA";
        String bizType = "B0";
        String encCd = "23";
        String mchtId = constant.PG_MID_AUTO;
        String mchtTrdNo = "GYAUTO" + UUID.randomUUID().toString().replace("-", "");
        LocalDateTime now = LocalDateTime.now();
        String trdDt = now.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String trdTm = now.toLocalTime().format(DateTimeFormatter.ofPattern("HHmmss"));

        params.put("mchtId", mchtId); // 헥토파이낸셜 부여 상점 아이디
        params.put("ver", ver); // 버전 (고정값)
        params.put("method", method); // 결제수단 (고정값)
        params.put("bizType", bizType); // 업무 구분 코드 (고정값)
        params.put("encCd", encCd);   // 암호화 구분 코드 (고정값)
        params.put("mchtTrdNo", mchtTrdNo); // 상점 주문번호
        params.put("trdDt", trdDt); // 주문 날짜
        params.put("trdTm", trdTm);   // 주문 시간


        if (requestMsg.equals("BillPaymentService")) {
            List<ClientDataContainer> agencyInfoList = agencyUseCase.selectAgencyInfo().stream()
                    .filter(ClientDataContainer::isActiveExtendableAutoRateAgency)
                    .collect(Collectors.toList());

            agencyInfoList.forEach(agencyInfo -> {
                Map<String, String> agencyMapData = agencyInfo.makeAgencyMapData(agencyInfo);
                Optional<ClientDataContainer> clientDataModel = agencyUseCase.getAgencyInfo(new ClientDataContainer(agencyInfo, agencyInfo));
                if (clientDataModel.isPresent()) {
                    ClientDataContainer info = clientDataModel.get();
                    if (info.isCheckedAgencyScheduledRateAutoPay(info)) {
                        AgencyProducts products = paymentUseCase.getAgencyProductByRateSel(info.getAgencyScheduledRateSel(info));

                        List<PaymentHistoryDataModel> paymentHistoryList = paymentUseCase.getPaymentHistoryByAgency(agencyInfo.getAgencyId(), convertSiteId)
                                .stream()
                                .filter((PaymentHistoryDataModel e) -> e.getTrTrace().equals(EnumTradeTrace.USED.getCode()))
                                .filter((PaymentHistoryDataModel e) -> e.getExtraAmountStatus().equals(EnumExtraAmountStatus.PASS.getCode()))
                                .collect(Collectors.toList());


                        Map<String, String> data = new HashMap<>();
                        String billKey = paymentHistoryList.get(0).getBillKey();

                        String productName = products.getName();

                        int excessAmount = 0;

                        Map<String, String> extendableAgencyId = info.checkedExtensionStatus(info);

                        if (paymentHistoryList.size() > 2) {
                            Map<String, Integer> excessMap = paymentUseCase.getExcessAmount(
                                    paymentUseCase.getPaymentHistoryByAgency(extendableAgencyId.get("agencyId"), extendableAgencyId.get("siteId"))
                            );
                            excessAmount = excessMap.get("excessAmount");
                        }

                        String amount = paymentUseCase.getAgencyProductByRateSel(info.getRateSel()).getPrice() + excessAmount;

                        data.put("pmtprdNm", productName);
                        data.put("mchtCustNm", "상점이름");
                        data.put("mchtCustId", mchtId);
                        data.put("billKey", billKey);
                        data.put("instmtMon", "00"); // 할부개월
                        data.put("crcCd", "KRW");
                        data.put("trdAmt", amount);

                        try {
                            data.put("pktHash", EncryptUtil.digestSHA256(trdDt + trdTm + mchtId + mchtTrdNo + data.get("trdAmt") + constant.LICENSE_KEY));
                            data.put("trdAmt", Base64.getEncoder().encodeToString(EncryptUtil.aes256EncryptEcb(constant.AES256_KEY, data.get("trdAmt"))));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        String url = constant.BILL_SERVER_URL + "/spay/APICardActionPay.do";

                        HttpClientUtil httpClientUtil = new HttpClientUtil();

                        responseData.put("params", params);
                        responseData.put("data", data);
                        String reqData = httpClientUtil.sendApi(url, responseData, 5000, 25000);

                        String hashCipher = "";
                        String hashPlain = trdDt + trdTm + mchtId + mchtTrdNo + data.get("trdAmt") + constant.LICENSE_KEY;

                        /** SHA256 해쉬 처리 */
                        try {
                            hashCipher = EncryptUtil.digestSHA256(hashPlain);//해쉬 값
                        } catch (Exception e) {
                            logger.error("[" + params.get("mchtTrdNo") + "][SHA256 HASHING] Hashing Fail! : " + e.toString());
                        } finally {
                            logger.info("[" + params.get("mchtTrdNo") + "][SHA256 HASHING] Plain Text[" + hashPlain + "] ---> Cipher Text[" + hashCipher + "]");
                        }
                        paymentUseCase.insertAutoPayPaymentHistory(agencyInfo.getAgencyId(), convertSiteId, paymentUseCase.getAgencyProductByRateSel(info.getRateSel()), reqData);
                    }
                }

            });
        }

    }


    /* 결제대기상태가 아닌 경우, 초기 결제로 판단 제휴사 승인대기, 통신사 승인대기 상태 전달. */

    private ResponseEntity<?> decideSiteStatus(Map<String, Object> responseMessage, ClientDataContainer searchedClient) {
        // TRADE_PENDING : 결제 대기 상태 (통신사 심사 승인 완료 이후 결제 대기 알림 발송완료 상태)
        // PENDING : 제휴사 승인 대기 ( 관리자가 등록하기 전 상태 )
        String status = searchedClient.getAgencyStatus(searchedClient);

        if (status.equals(EnumSiteStatus.SUSPENDED.getCode())) {
            responseMessage.put("resultCode", EnumResultCode.SuspendedSiteId.getCode());
            responseMessage.put("resultMsg", EnumResultCode.SuspendedSiteId.getValue());
            return ResponseEntity.ok(responseMessage);
        }

        if (!status.equals(EnumSiteStatus.TRADE_PENDING.getCode())) {
            if (status.equals(EnumSiteStatus.REJECT.getCode())) {
                responseMessage.put("resultCode", EnumResultCode.RejectAgency.getCode());
                responseMessage.put("resultMsg", EnumResultCode.RejectAgency.getValue());
                return ResponseEntity.ok(responseMessage);
            } else if (status.equals(EnumSiteStatus.PENDING.getCode())) {
                responseMessage.put("resultCode", EnumResultCode.PendingApprovalStatus.getCode());
                responseMessage.put("resultMsg", EnumResultCode.PendingApprovalStatus.getValue());
                return ResponseEntity.ok(responseMessage);
            }
        }
        return null;
    }

    private ResponseEntity<?> checkedScheduledRateSel(Map<String, Object> responseMessage, ClientDataContainer searchedClient) {
        boolean isAgencyScheduledRateAutoPay = searchedClient.isCheckedAgencyScheduledRateAutoPay(searchedClient);
        if (isAgencyScheduledRateAutoPay) {
            responseMessage.put("resultCode", EnumResultCode.Subscription.getCode());
            responseMessage.put("resultMsg", EnumResultCode.Subscription.getValue());
            return ResponseEntity.ok(responseMessage);
        }
        return null;
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


}
