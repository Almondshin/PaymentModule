package com.modules.link.service.agency;

import com.modules.link.domain.agency.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgencyService {

    private final AgencyRepository agencyRepository;

    public AgencyService(AgencyRepository agencyRepository) {
        this.agencyRepository = agencyRepository;
    }

    @Transactional
    public AgencyKey getAgencyKey(AgencyId agencyId) {
        return agencyRepository.findAgencyKey(agencyId);
    }

    @Transactional
    public Agency getAgencyBySiteId(SiteId siteId) {
        return agencyRepository.find(siteId);
    }

    @Transactional
    public void save(Agency agency) {
        agencyRepository.add(agency);
    }

}
