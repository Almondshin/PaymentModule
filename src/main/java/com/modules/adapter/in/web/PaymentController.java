package com.modules.adapter.in.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modules.adapter.in.models.ClientDataContainer;
import com.modules.adapter.in.models.PGDataContainer;
import com.modules.adapter.in.models.PaymentHistoryDataContainer;
import com.modules.adapter.out.payment.config.hectofinancial.Constant;
import com.modules.adapter.out.payment.utils.EncryptUtil;
import com.modules.adapter.out.payment.utils.HttpClientUtil;
import com.modules.application.domain.AgencyInfoKey;
import com.modules.application.domain.AgencyProducts;
import com.modules.application.enums.EnumAgency;
import com.modules.application.enums.EnumExtensionStatus;
import com.modules.application.enums.EnumSiteStatus;
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

        Optional<ClientDataContainer> optClientInfo = agencyUseCase.getAgencyInfo(clientDataContainer);

        List<Map<String, String>> productTypes = agencyUseCase.getProductTypes(clientDataContainer.agencyIdForRetrieve());
        Map<String, Object> responseMessage = new HashMap<>();

        if (optClientInfo.isPresent()) {
            ClientDataContainer clientInfo = optClientInfo.get();

            String rateSel = clientDataContainer.decideRateSel(clientInfo);
            String startDate = clientDataContainer.decideStartDate(clientInfo);

            ResponseEntity<?> siteStatusResponse = decideSiteStatus(responseMessage, clientInfo);
            if (siteStatusResponse != null) {
                return siteStatusResponse;
            }
            ResponseEntity<?> siteScheduledRateSelResponse = checkedScheduledRateSel(responseMessage, clientInfo);
            if (siteScheduledRateSelResponse != null) {
                return siteScheduledRateSelResponse;
            }
            Map<String, String> checkedExtensionStatus = clientInfo.checkedExtendable();
            if (!checkedExtensionStatus.isEmpty()) {
                Map<String, Integer> excessMap = paymentUseCase.getExcessAmount(
                        paymentUseCase.getPaymentHistoryByAgency(checkedExtensionStatus.get("agencyId"), checkedExtensionStatus.get("siteId"))
                );
                responseMessage.put("extensionStatus", EnumExtensionStatus.EXTENDABLE.getCode());
                responseMessage.put("excessCount", excessMap.get("excessCount"));
                responseMessage.put("excessAmount", excessMap.get("excessAmount"));
            }

            Map<String, String> companyInfoMap = clientDataContainer.makeCompanyInfo();
            responseMessage.put("clientInfo", companyInfoMap.get("companyName") + "," + companyInfoMap.get("bizNumber") + "," + companyInfoMap.get("ceoName"));
            responseMessage.put("rateSel", rateSel);
            responseMessage.put("startDate", startDate);
        }

        responseMessage.put("resultCode", EnumResultCode.SUCCESS.getCode());
        responseMessage.put("resultMsg", EnumResultCode.SUCCESS.getValue());
        responseMessage.put("profileUrl", profileSpecificUrl);
        responseMessage.put("profilePaymentUrl", profileSpecificPaymentUrl);
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

        //TODO
        //
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
        String merchantId = clientDataContainer.generateMerchantId(constant);

        responseMessage.put("resultCode", EnumResultCode.SUCCESS.getCode());
        responseMessage.put("resultMsg", EnumResultCode.SUCCESS.getValue());
        responseMessage.put("mchtId", merchantId);
        responseMessage.put("method", clientDataContainer.fetchPaymentMethod());
        responseMessage.put("trdDt", trdDt);
        responseMessage.put("trdTm", trdTm);
        responseMessage.put("mchtTrdNo", tradeNum);
        responseMessage.put("trdAmt", clientDataContainer.fetchPaymentPrice());
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
        String tradeNum = mapData.get("tradeNum");

        Optional<PaymentHistoryDataContainer> optPaymentHistory = paymentUseCase.getPaymentHistoryByAgency(agencyId, siteId)
                .stream()
                .filter(e -> e.isSameTradeNum(tradeNum))
                .findFirst();


        if (optPaymentHistory.isPresent()) {
            PaymentHistoryDataContainer paymentHistoryDataContainer = optPaymentHistory.get();
            Map<String, String> data = new HashMap<>();

            if (paymentHistoryDataContainer.isScheduledRateSel()) {
                mchtId = constant.getPG_CANCEL_MID_AUTO();
                params.put("mchtId", mchtId); // 헥토파이낸셜 부여 상점 아이디
            }
            String hashCipher = "";

            try {
                String pgTradeNumber = paymentHistoryDataContainer.pgTradeNumber();
                String paymentAmount = paymentHistoryDataContainer.paymentAmount();
                String amount = "";
                PGDataContainer dataContainer = new PGDataContainer(pgTradeNumber, paymentAmount, hashCipher);
                hashCipher = dataContainer.makeHashCipher(constant.LICENSE_KEY);
                amount = dataContainer.encodeAmount(constant.AES256_KEY);

                requestData.put("params", params);
                requestData.put("data", dataContainer);

                String url = constant.BILL_SERVER_URL + "/spay/APICancel.do";

                HttpClientUtil httpClientUtil = new HttpClientUtil();
                String resData = httpClientUtil.sendApi(url, requestData, 5000, 25000);
                System.out.println(resData);

                String hashPlain = trdDt + trdTm + mchtId + mchtTrdNo + amount + constant.LICENSE_KEY;
                hashCipher = EncryptUtil.digestSHA256(hashPlain);//해쉬 값

            } catch (Exception e) {
                logger.error("[" + params.get("mchtTrdNo") + "][SHA256 HASHING] Hashing Fail! : " + e.toString());
            } finally {
                logger.info("[" + params.get("mchtTrdNo") + "]Cipher Text[" + hashCipher + "]");
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
        LocalDateTime now = LocalDateTime.now();


        if (requestMsg.equals("BillPaymentService")) {
            List<ClientDataContainer> agencyInfoList = agencyUseCase.selectAgencyInfo()
                    .stream()
                    .filter(ClientDataContainer::isActiveAndExtendableSiteAndScheduledRateSel)
                    .collect(Collectors.toList());

            agencyInfoList.forEach(agencyInfo -> {
                Optional<ClientDataContainer> clientDataModel = agencyUseCase.getAgencyInfo(agencyInfo);
                if (clientDataModel.isPresent()) {
                    ClientDataContainer info = clientDataModel.get();
                    if (info.isScheduledPaymentEnabled()) {
                        Map<String, String> checkedExtensionStatus = info.checkedExtendable();
                        String siteId = checkedExtensionStatus.get("siteId");
                        String agencyId = checkedExtensionStatus.get("agencyId");

                        List<PaymentHistoryDataContainer> paymentHistoryList = paymentUseCase.getPaymentHistoryByAgency(agencyId, siteId)
                                .stream()
                                .filter(PaymentHistoryDataContainer::isActiveTradeTraceAndPassExtraAmountStatus)
                                .collect(Collectors.toList());

                        int excessAmount = 0;
                        if (paymentHistoryList.size() > 2) {
                            Map<String, Integer> excessMap = paymentUseCase.getExcessAmount(
                                    paymentUseCase.getPaymentHistoryByAgency(checkedExtensionStatus.get("agencyId"), checkedExtensionStatus.get("siteId"))
                            );
                            excessAmount = excessMap.get("excessAmount");
                        }

                        String amount = paymentUseCase.getAgencyProductByRateSel(info.rateSelForRetrieve("basic")).getPrice() + excessAmount;

                        AgencyProducts products = paymentUseCase.getAgencyProductByRateSel(info.rateSelForRetrieve("scheduled"));
                        String productName = products.getName();

                        String billKey = paymentHistoryList.get(0).retrieveBillKey();
                        String tradeNum = paymentUseCase.makeTradeNum();


                        String merchantId = constant.PG_MID_AUTO;
                        String hashCipher = "";

                        try {
                            PGDataContainer billParamsContainer = new PGDataContainer("bill_params", merchantId, tradeNum, productName, billKey, hashCipher, amount);
                            PGDataContainer billDataContainer = new PGDataContainer("bill_data", merchantId, tradeNum, productName, billKey, hashCipher, amount);
                            hashCipher = billParamsContainer.makeHashCipher(constant.LICENSE_KEY);

                            responseData.put("params", billParamsContainer);
                            responseData.put("data", billDataContainer);

                            String url = constant.BILL_SERVER_URL + "/spay/APICardActionPay.do";
                            HttpClientUtil httpClientUtil = new HttpClientUtil();

                            String reqData = httpClientUtil.sendApi(url, responseData, 5000, 25000);

                            paymentUseCase.insertAutoPayPaymentHistory(agencyId, siteId, paymentUseCase.getAgencyProductByRateSel(info.rateSelForRetrieve("basic")), reqData);

                        } catch (Exception e) {
                            logger.error("[" + tradeNum + "][SHA256 HASHING] Hashing Fail! : " + e.toString());
                            throw new RuntimeException(e);
                        } finally {
                            logger.info("[" + tradeNum + "][SHA256 HASHING] Cipher Text[" + hashCipher + "]");
                        }
                    }
                }

            });
        }
    }


    private ResponseEntity<?> decideSiteStatus(Map<String, Object> responseMessage, ClientDataContainer searchedClient) {
        // TRADE_PENDING : 결제 대기 상태 (통신사 심사 승인 완료 이후 결제 대기 알림 발송완료 상태)
        // PENDING : 제휴사 승인 대기 ( 관리자가 등록하기 전 상태 )
        EnumSiteStatus status = searchedClient.checkedSiteStatus();

        if (status == null || status == EnumSiteStatus.TRADE_PENDING) {
            return null;
        }

        switch (status) {
            case SUSPENDED:
                responseMessage.put("resultCode", EnumResultCode.SuspendedSiteId.getCode());
                responseMessage.put("resultMsg", EnumResultCode.SuspendedSiteId.getValue());
                break;
            case REJECT:
                responseMessage.put("resultCode", EnumResultCode.RejectAgency.getCode());
                responseMessage.put("resultMsg", EnumResultCode.RejectAgency.getValue());
                break;
            case PENDING:
                responseMessage.put("resultCode", EnumResultCode.PendingApprovalStatus.getCode());
                responseMessage.put("resultMsg", EnumResultCode.PendingApprovalStatus.getValue());
                break;
        }

        return ResponseEntity.ok(responseMessage);
    }


    private ResponseEntity<?> checkedScheduledRateSel(Map<String, Object> responseMessage, ClientDataContainer searchedClient) {
        boolean isAgencyScheduledRateAutoPay = searchedClient.isCheckedAgencyScheduledRateAutoPay();
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
