package com.modules.application.port.in;

import com.modules.adapter.in.models.ClientDataContainer;
import com.modules.adapter.in.models.PaymentHistoryDataModel;
import com.modules.application.domain.AgencyProducts;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface PaymentUseCase {
    void checkMchtParams(ClientDataContainer clientDataContainer) throws ParseException;
    String aes256EncryptEcb(ClientDataContainer clientDataContainer, String tradeNum, String trdDt, String trdTm);
    HashMap<String, String> encodeBase64(ClientDataContainer clientDataContainer, String tradeNum);
    List<PaymentHistoryDataModel> getPaymentHistoryByAgency(String agencyId,String siteId);
    PaymentHistoryDataModel getPaymentHistoryByTradeNum(String tradeNum);
    String makeTradeNum();
    AgencyProducts getAgencyProductByRateSel(String rateSel);
    Map<String, Integer> getExcessAmount(List<PaymentHistoryDataModel> list);
    void insertAutoPayPaymentHistory(String agencyId, String siteId, AgencyProducts product,String reqData);
    String makeTargetUrl(String agencyId,  String msgType);
}

