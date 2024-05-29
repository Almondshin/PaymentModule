package com.modules.link.service.agency;

import com.modules.link.domain.agency.Agency;
import com.modules.link.domain.agency.AgencyKey;
import com.modules.link.domain.agency.AgencyKeyRepository;
import com.modules.link.domain.agency.AgencyRepository;
import com.modules.link.domain.agency.SiteId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgencyRepositoryService {

    private final AgencyRepository agencyRepository;
    private final AgencyKeyRepository agencyKeyRepository;

    public AgencyRepositoryService(AgencyRepository agencyRepository, AgencyKeyRepository agencyKeyRepository) {
        this.agencyRepository = agencyRepository;
        this.agencyKeyRepository = agencyKeyRepository;
    }

    @Transactional
    public AgencyKey getAgencyKey(String agencyId) {
        return agencyKeyRepository.findById(agencyId)
                .orElseThrow(() -> new IllegalStateException("'" + agencyId + "'의 제휴사는 존재하지 않는 제휴사 ID입니다."));
    }

    @Transactional
    public Agency getAgencyBySiteId(SiteId siteId) {
        return agencyRepository.findById(siteId)
                .orElseThrow(() -> new IllegalStateException("'" + siteId + "'의 이용기관은 존재하지 않는 사이트 ID입니다."));
    }

    @Transactional
    public void save(Agency agency) {
        agencyRepository.save(agency);
    }
}
