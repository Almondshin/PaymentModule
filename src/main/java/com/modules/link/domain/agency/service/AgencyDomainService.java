package com.modules.link.domain.agency.service;

import com.modules.link.domain.agency.Agency;
import org.springframework.stereotype.Component;

@Component
public class AgencyDomainService {

    public String generateTargetData(Agency agency, String statusType) {
        return agency.makeVerifyAndEncryptData(statusType);
    }
}
