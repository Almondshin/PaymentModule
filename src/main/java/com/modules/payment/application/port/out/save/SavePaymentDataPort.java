package com.modules.payment.application.port.out.save;

import com.modules.payment.domain.PaymentHistory;

public interface SavePaymentDataPort {
    void insertPayment(PaymentHistory paymentHistory);
    void updatePayment(PaymentHistory paymentHistory);
    void updatePaymentUseCount(PaymentHistory paymentHistory, long useCountSum);
    void updatePaymentExtraAmountStatus(PaymentHistory paymentHistory);

}
