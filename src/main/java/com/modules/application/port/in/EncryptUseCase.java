package com.modules.application.port.in;

import com.modules.adapter.in.models.ClientDataContainer;

import java.util.Map;

public interface EncryptUseCase {
    String mapToJSONString(Map<String, String> map);
    Map<String, String> getKeyIv(String agencyId);
    String encryptData(String targetEncode, Map<String,String> keyIv);
    byte[] decryptData(ClientDataContainer clientDataContainer, Map<String,String> keyIv);
    String hmacSHA256(String target, String hmacKeyString);
}
