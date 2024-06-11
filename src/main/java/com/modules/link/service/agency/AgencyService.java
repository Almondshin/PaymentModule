package com.modules.link.service.agency;

import com.modules.link.domain.agency.*;
import com.modules.link.domain.agency.service.AgencyDomainService;
import com.modules.link.enums.EnumResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;

@Slf4j
@Service
public class AgencyService {
    private static final String STATUS_TYPE = "status";

    private final AgencyRepository agencyRepository;
    private final AgencyDomainService agencyDomainService;


    public AgencyService(AgencyRepository agencyRepository, AgencyDomainService agencyDomainService) {
        this.agencyRepository = agencyRepository;
        this.agencyDomainService = agencyDomainService;
    }


    @Transactional
    public Site getSite(SiteId siteId) {
        return agencyRepository.findSite(siteId);
    }

    @Transactional
    public AgencyKey getAgencyKey(AgencyId agencyId) {
        return agencyRepository.findAgencyKey(agencyId);
    }

    @Transactional
    public Agency getAgency(SiteId siteId) {
        return agencyRepository.find(siteId);
    }

    @Transactional
    public String generateSiteStatusData(SiteId siteId) {
        Agency agency = getAgency(siteId);
        return agencyDomainService.generateTargetData(agency, STATUS_TYPE);
    }

    @Transactional
    public void save(Agency agency) {
        if (getSite(agency.getId()) != null || getAgency(agency.getId()) != null) {
            throw new EntityExistsException(EnumResultCode.DuplicateMember.getMessage());
        }
        agencyRepository.add(agency);
    }

}
