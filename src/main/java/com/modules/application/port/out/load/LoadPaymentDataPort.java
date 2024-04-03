package com.modules.application.port.out.load;

import com.modules.application.domain.Agency;
import com.modules.application.domain.PaymentHistory;

import java.util.List;
import java.util.Optional;

public interface LoadPaymentDataPort {
    List<PaymentHistory> getPaymentHistoryByAgency(Agency agency);
    Optional<PaymentHistory> getPaymentHistoryByTradeNum(String tradeNum);

}
