package com.modules.link.application.service.agency;

import com.modules.link.domain.agency.*;
import com.modules.link.domain.agency.service.AgencyDomainService;
import com.modules.link.application.service.exception.EntityNotFoundException;
import com.modules.link.application.service.exception.NoSuchFieldException;
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
    private static final Logger logger = LoggerFactory.getLogger(AgencyService.class);


    private final AgencyRepository agencyRepository;
    private final AgencyKeyRepository agencyKeyRepository;
    private final AgencyDomainService agencyDomainService;
    private final Validator validator;


    public AgencyService(AgencyRepository agencyRepository, AgencyKeyRepository agencyKeyRepository, AgencyDomainService agencyDomainService, Validator validator) {
        this.agencyRepository = agencyRepository;
        this.agencyKeyRepository = agencyKeyRepository;
        this.agencyDomainService = agencyDomainService;
        this.validator = validator;
    }

    public AgencyKey getAgencyKey(AgencyId agencyId) {
        return agencyKeyRepository.find(agencyId);
    }


    @Transactional(readOnly = true)
    public String generateSiteStatusData(SiteId siteId) {
        Agency agency = getAgency(siteId);
        if (Objects.isNull(agency)) {
            return agencyDomainService.generateNotFoundStatusData(siteId);
        }
        return agencyDomainService.generateTargetData(agency, STATUS_TYPE);
    }

    @Transactional(readOnly = true)
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
        Site existingSite = agencyRepository.find(siteId).getSite();
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

    private Agency getAgency(SiteId siteId) {
        return agencyRepository.find(siteId);
    }

//    public void testJpa(AgencyId agencyId){
//        System.out.println(agencyKeyRepository.find(agencyId).getProducts().toString());
//    }
}
