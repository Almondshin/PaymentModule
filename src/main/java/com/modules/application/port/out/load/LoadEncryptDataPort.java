package com.modules.application.port.out.load;

import com.modules.application.domain.AgencyInfoKey;

import java.util.Optional;

public interface LoadEncryptDataPort {
    Optional<AgencyInfoKey> getAgencyInfoKey(String agencyId);

}
