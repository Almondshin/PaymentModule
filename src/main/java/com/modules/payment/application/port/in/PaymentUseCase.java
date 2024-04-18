package com.modules.payment.application.port.in;

import com.modules.payment.domain.Agency;
import com.modules.payment.domain.PaymentHistory;
import com.modules.payment.domain.Product;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PaymentUseCase {
    List<PaymentHistory> getPaymentHistoryByAgency(Agency agency);
    Optional<PaymentHistory> getPaymentHistoryByTradeNum(String tradeNum);
    String makeTradeNum();
    Product getAgencyProductByRateSel(String rateSel);
    Map<String, Integer> getExcessAmount(List<PaymentHistory> list);
    void insertAutoPayPaymentHistory(Agency agency, Product product,String reqData);

    void processAgencyPayment(Agency info, List<PaymentHistory> paymentHistoryList);

    String processPayment(Map<String, Object> requestData, String url);

    Map<String, Object>  prepareCancelRequestData(PaymentHistory paymentHistory);
}