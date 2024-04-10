package com.modules.payment.application.port.out.load;

import com.modules.payment.application.domain.AgencyInfoKey;

import java.util.Optional;

public interface LoadEncryptDataPort {
    Optional<AgencyInfoKey> getAgencyInfoKey(String agencyId);

}
