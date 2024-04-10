package com.modules.payment.application.port.in;

import com.modules.payment.domain.Agency;

import java.util.Map;

public interface EncryptUseCase {
    String mapToJSONString(Map<String, String> map);
    Map<String, String> getKeyIv(String agencyId);
    String encryptData(String targetEncode, Map<String,String> keyIv);
    byte[] decryptData(Agency agency, Map<String,String> keyIv);
    String hmacSHA256(String target, String hmacKeyString);
}
