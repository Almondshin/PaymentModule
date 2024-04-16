package com.modules.payment.adapter.in.web;

import com.modules.payment.application.config.Constant;
import com.modules.payment.application.port.in.AgencyUseCase;
import com.modules.payment.application.port.in.PaymentUseCase;
import com.modules.payment.application.utils.HttpClientUtil;
import com.modules.payment.application.utils.Utils;
import com.modules.payment.domain.Agency;
import com.modules.payment.domain.PGDataContainer;
import com.modules.payment.domain.PaymentHistory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PGController {

    private final Constant constant;
    private final AgencyUseCase agencyUseCase;
    private final PaymentUseCase paymentUseCase;

    public PGController(Constant constant, AgencyUseCase agencyUseCase, PaymentUseCase paymentUseCase) {
        this.constant = constant;
        this.agencyUseCase = agencyUseCase;
        this.paymentUseCase = paymentUseCase;
    }


    /**
     * 카드 취소 요청
     *
     * @param requestSiteId the request site id
     */
    @PostMapping(value = "/cardCancel")
    public void requestCardCancelPayment(@RequestBody String requestSiteId) {
        PaymentHistory paymentHistory = Optional.ofNullable(Utils.jsonStringToObject(requestSiteId, PaymentHistory.class))
                .orElseThrow(() -> new IllegalArgumentException("Not found PaymentHistory for tradeNum : " + requestSiteId));
        Map<String, Object> requestData = prepareCancelRequestData(paymentHistory);
        processPayment(requestData, constant.PAYMENT_BILL_SERVER_URL + "/spay/APICancel.do");
    }

    /**
     * 정기 결제 요청
     *
     * @param requestMsg the request msg
     */
    @PostMapping(value = "/bill")
    public void requestBillKeyPayment(@RequestBody String requestMsg) {
        if (requestMsg.equals("BillPaymentService")) {
            processBillPayment();
        }
    }

    private Map<String, Object> prepareCancelRequestData(PaymentHistory paymentHistory) {
        Map<String, Object> requestData = new HashMap<>();
        String merchantId = paymentHistory.isScheduledRateSel() ? constant.getPAYMENT_PG_CANCEL_MID_AUTO() : constant.getPAYMENT_PG_CANCEL_MID_CARD();
        String amount = paymentHistory.paymentAmount();
        String tradeNum = paymentUseCase.makeTradeNum();
        requestData.put("params", new PGDataContainer("cancel_params", merchantId, tradeNum, amount));
        requestData.put("data", new PGDataContainer("cancel_data", "", "", amount));
        return requestData;
    }

    private void processBillPayment() {
        agencyUseCase.selectAgencyInfo()
                .stream()
                .filter(Agency::isCurrentScheduledRateSel)
                .forEach(agencyInfo -> agencyUseCase.getAgencyInfo(agencyInfo).ifPresent(info -> {
                    List<PaymentHistory> paymentHistoryList = paymentUseCase.getPaymentHistoryByAgency(info.checkedExtendable())
                            .stream()
                            .filter(PaymentHistory::isPassed)
                            .collect(Collectors.toList());

                    if (!paymentHistoryList.isEmpty()) {
                        processAgencyPayment(info, paymentHistoryList);
                    }
                }));
    }

    private void processAgencyPayment(Agency info, List<PaymentHistory> paymentHistoryList) {
        int excessAmount = 0;
        if (paymentHistoryList.size() > 2) {
            Map<String, Integer> excessMap = paymentUseCase.getExcessAmount(paymentUseCase.getPaymentHistoryByAgency(info.checkedExtendable()));
            excessAmount = excessMap.get("excessAmount");
        }

        String productName = paymentUseCase.getAgencyProductByRateSel(info.selectRateSelBasedOnType("scheduled")).productName();
        String merchantId = constant.PAYMENT_PG_MID_AUTO;
        String tradeNum = paymentUseCase.makeTradeNum();

        PaymentHistory paymentHistory = paymentHistoryList.get(0);
        String billKey = paymentHistory.retrieveBillKey();
        String amount = paymentHistory.paymentAmount() + excessAmount;

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("params", paymentHistory.pgDataContainer("bill_params", merchantId, tradeNum, "", "", ""));
        requestData.put("data", paymentHistory.pgDataContainer("bill_data", merchantId, tradeNum, billKey, amount, productName));

        String responseData = processPayment(requestData, constant.PAYMENT_BILL_SERVER_URL + "/spay/APICardActionPay.do");
        paymentUseCase.insertAutoPayPaymentHistory(info, paymentUseCase.getAgencyProductByRateSel(info.selectRateSelBasedOnType("basic")), responseData);
    }

    private String processPayment(Map<String, Object> requestData, String url) {
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        String response = httpClientUtil.sendApi(url, requestData, 5000, 25000);
        System.out.println(response);
        return response;
    }
}
