package com.modules.payment.adapter.in.web;

import com.modules.payment.application.config.Constant;
import com.modules.payment.application.port.in.AgencyUseCase;
import com.modules.payment.application.port.in.PaymentUseCase;
import com.modules.payment.application.utils.Utils;
import com.modules.payment.domain.Agency;
import com.modules.payment.domain.PaymentHistory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
        Map<String, Object> requestData = paymentUseCase.prepareCancelRequestData(paymentHistory);
        paymentUseCase.processPayment(requestData, constant.PAYMENT_BILL_SERVER_URL + "/spay/APICancel.do");
    }

    /**
     * 정기 결제 요청
     *
     * @param requestMsg the request msg
     */
    @PostMapping(value = "/bill")
    public void requestBillKeyPayment(@RequestBody String requestMsg) {
        if (requestMsg.equals("BillPaymentService")) {
            agencyUseCase.selectAgencyInfo()
                    .stream()
                    .filter(Agency::isCurrentScheduledRateSel)
                    .forEach(agencyInfo -> agencyUseCase.getAgencyInfo(agencyInfo).ifPresent(info -> {
                        List<PaymentHistory> paymentHistoryList = paymentUseCase.getPaymentHistoryByAgency(info.checkedExtendable())
                                .stream()
                                .filter(PaymentHistory::isPassed)
                                .collect(Collectors.toList());

                        if (!paymentHistoryList.isEmpty()) {
                            paymentUseCase.processAgencyPayment(info, paymentHistoryList);
                        }
                    }));
        }
    }


}
