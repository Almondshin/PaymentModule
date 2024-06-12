package com.modules.link.service.agency;

import com.modules.link.domain.agency.*;
import com.modules.link.domain.agency.service.AgencyDomainService;
import com.modules.link.service.exception.EntityNotFoundException;
import com.modules.link.service.exception.NoSuchFieldException;
import com.modules.link.enums.EnumResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AgencyService {
    private static final String STATUS_TYPE = "status";
    private static final String CANCEL_TYPE = "cancel";
    private static final Logger log = LoggerFactory.getLogger(AgencyService.class);


    private final AgencyRepository agencyRepository;
    private final AgencyDomainService agencyDomainService;
    private final Validator validator;


    public AgencyService(AgencyRepository agencyRepository, AgencyDomainService agencyDomainService, Validator validator) {
        this.agencyRepository = agencyRepository;
        this.agencyDomainService = agencyDomainService;
        this.validator = validator;
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
        if (Objects.isNull(agency)) {
            return agencyDomainService.generateNotFoundStatusData(siteId);
        }
        return agencyDomainService.generateTargetData(agency, STATUS_TYPE);
    }

    @Transactional
    public String generateCancelData(SiteId siteId) {
        Agency agency = getAgency(siteId);
        if (Objects.isNull(agency)) {
            throw new EntityNotFoundException(EnumResultCode.UnregisteredAgency, siteId.toString());
        }
        return agencyDomainService.generateTargetData(agency, CANCEL_TYPE);
    }


    @Transactional
    @Validated
    public void save(SiteId siteId, Agency agency) {
        Site existingSite = getSite(siteId);
        Agency existingAgency = getAgency(agency.getId());
        agency.addSite(existingAgency, existingSite);
        Set<String> missingFields = validateNotNullFields(agency);
        if (!missingFields.isEmpty()) {
            String missingField = String.join(", ", missingFields);
            throw new NoSuchFieldException(EnumResultCode.NoSuchFieldError, missingField);
        }
        agencyRepository.add(agency);
    }

    private Set<String> validateNotNullFields(Agency agency) {
        Set<ConstraintViolation<Agency>> violations = validator.validate(agency);
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }


}
