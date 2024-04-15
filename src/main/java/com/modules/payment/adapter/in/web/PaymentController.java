package com.modules.payment.adapter.in.web;

import com.modules.payment.application.config.Constant;
import com.modules.payment.application.enums.EnumExtensionStatus;
import com.modules.payment.application.exceptions.exceptions.IllegalStatusException;
import com.modules.payment.application.port.in.AgencyUseCase;
import com.modules.payment.application.port.in.PaymentUseCase;
import com.modules.payment.application.utils.HttpClientUtil;
import com.modules.payment.application.utils.Utils;
import com.modules.payment.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    public PaymentController(AgencyUseCase agencyUseCase, Constant constant, PaymentUseCase paymentUseCase) {
        this.agencyUseCase = agencyUseCase;
        this.constant = constant;
        this.paymentUseCase = paymentUseCase;
    }


    /**
     * 결제 정보 요청
     *
     * @param agency 필수 값 : AgencyId, SiteId , 옵션 값 : RateSel, StartDate
     * @return resultCode, resultMsg, siteId, RateSel list,
     */
    @PostMapping("/getPaymentInfo")
    public ResponseEntity<?> getPaymentInfo(@RequestBody Agency agency) {
        List<Map<String, String>> productTypes;
        List<String> clientInfo;
        String rateSel, startDate;
        int excessCount = 0;
        int excessAmount = 0;
        ResponseManager manager;
        try {
            Agency agencyInfo = agencyUseCase.getAgencyInfo(agency)
                    .orElseThrow(() -> new IllegalArgumentException("Agency not found"));
            clientInfo = agencyInfo.makeCompanyInfo();
            rateSel = agency.rateSel(agencyInfo);
            startDate = agency.startDate(agencyInfo);
            productTypes = agencyUseCase.getProductTypes(agency.agencyId());
            agencyInfo.isActive();
            agencyInfo.isScheduledRateSel();

            if (agencyInfo.isExtendable()) {
                Map<String, Integer> excessMap = paymentUseCase.getExcessAmount(
                        paymentUseCase.getPaymentHistoryByAgency(agencyInfo.checkedExtendable()));
                excessCount = excessMap.get("excessCount");
                excessAmount = excessMap.get("excessAmount");
            }

            manager = new ResponseManager(clientInfo, rateSel, startDate, profileSpecificUrl, profileSpecificPaymentUrl, productTypes, agencyInfo.isExtendable(), EnumExtensionStatus.EXTENDABLE.getCode(), excessCount, excessAmount);

        } catch (IllegalStatusException e) {
            return ResponseEntity.ok(e.getEnumResultCode());
        }

        return ResponseEntity.ok(manager);
    }

    /**
     * Sets payment site info.
     *
     * @param agency the client data model
     * @return the payment site info
     */
    @PostMapping("/setPaymentSiteInfo")
    public ResponseEntity<?> setPaymentSiteInfo(@RequestBody Agency agency) {
        List<PaymentHistory> list = paymentUseCase.getPaymentHistoryByAgency(agency).stream()
                .filter(PaymentHistory::isPassed)
                .collect(Collectors.toList());

        double excessAmount = 0;
        if (list.size() > 2){
            Map<String, Integer> excessMap = paymentUseCase.getExcessAmount(
                    paymentUseCase.getPaymentHistoryByAgency(agency.checkedExtendable()));
            excessAmount = excessMap.get("excessAmount");
        }

        Product product = paymentUseCase.getAgencyProductByRateSel(agency.rateSel(agency));
        agency.checkedParams(product,excessAmount);

        String tradeNum = paymentUseCase.makeTradeNum();
        PGResponseManager manager = agency.pgResponseMsg(tradeNum);
        return ResponseEntity.ok(manager);
    }


    @PostMapping(value = "/cardCancel")
    public void requestCardCancelPayment(@RequestBody String requestSiteId) {

        PaymentHistory convertPaymentHistory = Optional.ofNullable(Utils.jsonStringToObject(requestSiteId, PaymentHistory.class))
                .orElseThrow(() -> new IllegalArgumentException("Not found PaymentHistory for tradeNum : " + requestSiteId));

        paymentUseCase.getPaymentHistoryByTradeNum(convertPaymentHistory.tradeNum()).ifPresent(paymentHistory -> {
            Map<String, Object> requestData = new HashMap<>();

            String hashCipher = "";
            String merchantId = constant.getPAYMENT_PG_CANCEL_MID_CARD();
            String tradeNum = paymentUseCase.makeTradeNum();

            if (paymentHistory.isScheduledRateSel()) {
                merchantId = constant.getPAYMENT_PG_CANCEL_MID_AUTO();
            }

            String amount = paymentHistory.paymentAmount();
            PGDataContainer params = new PGDataContainer("cancel_params", merchantId, tradeNum, amount);
            PGDataContainer data = new PGDataContainer("cancel_data", "", "", amount);

            requestData.put("params", params);
            requestData.put("data", data);
            String url = constant.PAYMENT_BILL_SERVER_URL + "/spay/APICancel.do";

            try {
                hashCipher = params.makeHashCipher(constant.PAYMENT_LICENSE_KEY);

                HttpClientUtil httpClientUtil = new HttpClientUtil();
                String resData = httpClientUtil.sendApi(url, requestData, 5000, 25000);
                System.out.println(resData);

            } catch (Exception e) {
                logger.error("[" + tradeNum + "][SHA256 HASHING] Hashing Fail! : " + e.toString());
                throw new RuntimeException(e);
            } finally {
                logger.info("[" + tradeNum + "][SHA256 HASHING] Cipher Text[" + hashCipher + "]");
            }
        });
    }

    @PostMapping(value = "/bill")
    public void requestBillKeyPayment(@RequestBody String requestMsg) {

        if (requestMsg.equals("BillPaymentService")) {
            agencyUseCase.selectAgencyInfo()
                    .stream()
                    .filter(Agency::isCurrentScheduledRateSel)
                    .forEach(agencyInfo -> {
                        agencyUseCase.getAgencyInfo(agencyInfo).ifPresent(info -> {
                            List<PaymentHistory> paymentHistoryList = paymentUseCase.getPaymentHistoryByAgency(info.checkedExtendable())
                                    .stream()
                                    .filter(PaymentHistory::isPassed)
                                    .collect(Collectors.toList());

                            if (!paymentHistoryList.isEmpty()) {
                                int excessAmount = 0;
                                if (paymentHistoryList.size() > 2) {
                                    Map<String, Integer> excessMap = paymentUseCase.getExcessAmount(
                                            paymentUseCase.getPaymentHistoryByAgency(info.checkedExtendable())
                                    );
                                    excessAmount = excessMap.get("excessAmount");
                                }
                                PaymentHistory paymentHistory = paymentHistoryList.get(0);

                                String amount = paymentHistory.paymentAmount() + excessAmount;
                                String productName = paymentUseCase.getAgencyProductByRateSel(info.selectRateSelBasedOnType("scheduled")).productName();

                                String billKey = paymentHistory.retrieveBillKey();
                                String tradeNum = paymentUseCase.makeTradeNum();

                                String merchantId = constant.PAYMENT_PG_MID_AUTO;
                                String hashCipher = "";

                                PGDataContainer params = paymentHistory.pgDataContainer("bill_params", merchantId, tradeNum, "", "", "");
                                PGDataContainer data = paymentHistory.pgDataContainer("bill_data", merchantId, tradeNum, billKey, amount, productName);

                                try {
                                    hashCipher = params.makeHashCipher(constant.PAYMENT_LICENSE_KEY);
                                    Map<String, Object> responseData = new HashMap<>();
                                    responseData.put("params", params);
                                    responseData.put("data", data);
                                    String url = constant.PAYMENT_BILL_SERVER_URL + "/spay/APICardActionPay.do";
                                    HttpClientUtil httpClientUtil = new HttpClientUtil();
                                    String reqData = httpClientUtil.sendApi(url, responseData, 5000, 25000);
                                    paymentUseCase.insertAutoPayPaymentHistory(info, paymentUseCase.getAgencyProductByRateSel(info.selectRateSelBasedOnType("basic")), reqData);

                                } catch (Exception e) {
                                    logger.error("[" + tradeNum + "][SHA256 HASHING] Hashing Fail! : " + e.toString());
                                    throw new RuntimeException(e);
                                } finally {
                                    logger.info("[" + tradeNum + "][SHA256 HASHING] Cipher Text[" + hashCipher + "]");
                                }
                            }
                        });
                    });
        }
    }

}
