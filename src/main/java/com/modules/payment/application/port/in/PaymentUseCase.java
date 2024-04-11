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
    void checkMchtParams(Agency agency);
    String aes256EncryptEcb(Agency agency, String tradeNum, String trdDt, String trdTm);
    HashMap<String, String> encodeBase64(Agency agency, String tradeNum);
    List<PaymentHistory> getPaymentHistoryByAgency(Agency agency);
    Optional<PaymentHistory> getPaymentHistoryByTradeNum(String tradeNum);
    String makeTradeNum();
    Product getAgencyProductByRateSel(String rateSel);
    Map<String, Integer> getExcessAmount(List<PaymentHistory> list);
    Map<String, Integer> getExcessAmount(PaymentHistory paymentHistory);
    void insertAutoPayPaymentHistory(Agency agency, Product product,String reqData);
    String makeTargetUrl(String agencyId,  String msgType);
}