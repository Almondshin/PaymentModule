package com.modules.payment.application.port.in;

import com.modules.payment.domain.Agency;
import com.modules.payment.domain.AgencyInfoKey;

import java.util.Map;

public interface EncryptUseCase {
    String mapToJSONString(Map<String, String> map);
    AgencyInfoKey getKeyIv(String agencyId);
    String encryptData(String targetEncode, AgencyInfoKey keyIv);
    byte[] decryptData(Agency agency, Map<String,String> keyIv);
    String hmacSHA256(String target, String hmacKeyString);
}
