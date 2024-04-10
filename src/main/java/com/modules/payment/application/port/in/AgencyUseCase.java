package com.modules.payment.application.port.in;

import com.modules.payment.domain.Agency;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AgencyUseCase {
    void registerAgency(Agency agency); // 제휴사 등록
    Optional<Agency> getAgencyInfo(Agency agency);  // Agency 객체를 전달받는 방식으로 변경
    List<Agency> selectAgencyInfo();
    List<Map<String, String>> getProductTypes(String agencyId);

}

