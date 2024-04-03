package com.modules.application.port.in;

import com.modules.adapter.in.models.ClientDataContainer;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AgencyUseCase {
    void registerAgency(ClientDataContainer clientDataContainer); // 제휴사 등록
    Optional<ClientDataContainer> getAgencyInfo(ClientDataContainer clientDataContainer);  // Agency 객체를 전달받는 방식으로 변경
    List<ClientDataContainer> selectAgencyInfo();
    List<Map<String, String>> getProductTypes(String agencyId);

}

