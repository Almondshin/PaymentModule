package com.modules.application.port.out.save;

import com.modules.application.domain.Agency;
import com.modules.application.domain.Client;
import com.modules.application.domain.SettleManager;

public interface SaveAgencyDataPort {
    void registerAgency(Agency agency, Client client, SettleManager settleManager);
    void updateAgency(Agency agency, Client client, String paymentStatus);

    void updateAgencyExcessCount(Agency agency, int excessCount);
}
