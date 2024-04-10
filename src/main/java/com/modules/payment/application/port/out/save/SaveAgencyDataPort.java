package com.modules.payment.application.port.out.save;

import com.modules.payment.application.domain.Agency;
import com.modules.payment.application.domain.Client;
import com.modules.payment.application.domain.SettleManager;

public interface SaveAgencyDataPort {
    void registerAgency(Agency agency, Client client, SettleManager settleManager);
    void updateAgency(Agency agency, Client client, String paymentStatus);

    void updateAgencyExcessCount(Agency agency, int excessCount);
}
