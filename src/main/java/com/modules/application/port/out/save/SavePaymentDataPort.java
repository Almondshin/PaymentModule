package com.modules.application.port.out.save;

import com.modules.application.domain.PaymentHistory;

public interface SavePaymentDataPort {
    void insertPayment(PaymentHistory paymentHistory);
    void updatePayment(PaymentHistory paymentHistory);
    void updatePaymentUseCount(String tradeNum, String pgTradeNum, long useCountSum);
    void updatePaymentExtraAmountStatus(PaymentHistory paymentHistory);

}
