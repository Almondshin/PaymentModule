package com.modules.link.domain.agency.service;

import com.modules.link.domain.agency.Agency;
import com.modules.link.domain.agency.AgencyKey;
import com.modules.link.utils.AuthUtils;
import org.springframework.stereotype.Component;

@Component
public class AgencyDomainService {

    public String generateTargetDate(Agency agency, String statusType) {
        return agency.makeVerifyAndEncryptData(statusType);
    }

    public String encryptData(AgencyKey agencyKey, String data) {
        return agencyKey.encryptData(data);
    }

    public String generateHmac(AgencyKey agencyKey, String data) {
        return AuthUtils.hmacSHA256(data, agencyKey.keyString());
    }

    public String extractOriginalMessage(AgencyKey agencyKey, String encryptData) {
        return agencyKey.originalMessage(encryptData);
    }
}
