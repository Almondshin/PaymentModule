package com.modules.payment.application.port.out.save;


import com.modules.payment.domain.Agency;

public interface SaveAgencyDataPort {
    void registerAgency(Agency agency);
    void updateAgency(Agency agency, String paymentStatus);

    void updateAgencyExcessCount(Agency agency, int excessCount);

}
