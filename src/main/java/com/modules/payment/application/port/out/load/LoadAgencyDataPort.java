package com.modules.payment.application.port.out.load;

import com.modules.payment.domain.Agency;

import java.util.List;
import java.util.Optional;

public interface LoadAgencyDataPort {
    Optional<Agency> getAgencyInfo(com.modules.payment.application.domain.Agency agency, com.modules.payment.application.domain.Client client);

    List<Agency> selectAgencyInfo();
}

