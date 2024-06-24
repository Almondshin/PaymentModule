package com.modules.link.application.service.agency;

import com.modules.link.application.service.exception.EntityExistsException;
import com.modules.link.domain.agency.*;
import com.modules.link.domain.agency.service.AgencyDomainService;
import com.modules.link.enums.EnumResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;


@Service
public class AgencyService {
    private static final String STATUS_TYPE = "status";
    private static final String CANCEL_TYPE = "cancel";
    private static final Logger logger = LoggerFactory.getLogger(AgencyService.class);


    private final AgencyRepository agencyRepository;
    private final SiteRepository siteRepository;
    private final AgencyKeyRepository agencyKeyRepository;
    private final AgencyDomainService agencyDomainService;


    public AgencyService(AgencyRepository agencyRepository, SiteRepository siteRepository, AgencyKeyRepository agencyKeyRepository, AgencyDomainService agencyDomainService) {
        this.agencyRepository = agencyRepository;
        this.siteRepository = siteRepository;
        this.agencyKeyRepository = agencyKeyRepository;
        this.agencyDomainService = agencyDomainService;
    }

    @Transactional
    public AgencyKey getAgencyKey(AgencyId agencyId) {
        return agencyKeyRepository.find(agencyId);
    }

    private Agency getAgency(SiteId siteId) {
        return agencyRepository.find(siteId);
    }

    @Transactional(readOnly = true)
    public String generateSiteStatusData(SiteId siteId) {
        Agency agency = getAgency(siteId);
        return agencyDomainService.generateTargetData(agency, STATUS_TYPE);
    }

    @Transactional(readOnly = true)
    public String generateCancelData(SiteId siteId) {
        Agency agency = getAgency(siteId);
        return agencyDomainService.generateTargetData(agency, CANCEL_TYPE);
    }

    @Transactional
    @Validated
    public void save(SiteId siteId, Agency agency) {
        agencyDomainService.validateAgency(agency);

        if (getAgency(siteId) != null) {
            logger.error("SiteId로 등록된 제휴사가 존재합니다. : {}", siteId.toString());
            throw new EntityExistsException(EnumResultCode.DuplicateMember, siteId.toString());
        }

        if (siteRepository.find(siteId) != null){
            logger.error("SiteId로 등록된 사이트가 존재합니다. : {}", siteId.toString());
            throw new EntityExistsException(EnumResultCode.DuplicateMember, siteId.toString());
        }

        Agency newAgency = agency.addSite();
        agencyRepository.add(newAgency);
    }

}
