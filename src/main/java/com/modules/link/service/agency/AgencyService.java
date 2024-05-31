package com.modules.link.service.agency;

import com.modules.link.controller.container.AgencyReceived;
import com.modules.link.domain.agency.*;
import com.modules.link.domain.agency.service.AgencyDomainService;
import com.modules.link.enums.EnumAgency;
import com.modules.link.enums.EnumResultCode;
import com.modules.link.service.agency.AgencyDtos.AgencyResponse;
import com.modules.link.service.agency.AgencyDtos.CancelInfo;
import com.modules.link.service.agency.AgencyDtos.RegisterInfo;
import com.modules.link.service.agency.AgencyDtos.StatusInfo;
import com.modules.link.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AgencyService {
    private static final String STATUS_TYPE = "status";
    private static final String SITE_INFO = "info";
    private static final String REGISTER_TYPE = "reg";
    private static final String CANCEL_TYPE = "cancel";


    @Value("${external.admin.url}")
    private String profileSpecificAdminUrl;

    private final AgencyRepository agencyRepository;
    private final AgencyDomainService agencyDomainService;
    private final Validator validator;


    public AgencyService(AgencyRepository agencyRepository, AgencyDomainService agencyDomainService, Validator validator) {
        this.agencyRepository = agencyRepository;
        this.agencyDomainService = agencyDomainService;
        this.validator = validator;
    }

    public Optional<AgencyResponse> validateHmacAndMsgType(AgencyKey agencyKey, String msgType, String encryptData, String verifyInfo) {
        return agencyKey.validateHmacAndMsgType(msgType, encryptData, verifyInfo).map(AgencyResponse::new);
    }

    @Transactional
    public AgencyResponse getSiteStatus(AgencyKey agencyKey, AgencyReceived receivedData) {
        String originalMessage = agencyDomainService.extractOriginalMessage(agencyKey, receivedData.getEncryptData());
        StatusInfo statusInfo = Utils.jsonStringToObject(originalMessage, StatusInfo.class);
        Agency agency = getAgency(SiteId.of(statusInfo.getSiteId()));
        String targetDate = agencyDomainService.generateTargetDate(agency, STATUS_TYPE);
        String messageType = EnumAgency.getMsgType(agencyKey.keyString(), SITE_INFO);

        String encryptedData = agencyDomainService.encryptData(agencyKey, targetDate);
        String hmac = agencyDomainService.generateHmac(agencyKey, targetDate);

        return new AgencyResponse(encryptedData, hmac, messageType);
    }

    @Transactional
    @Validated
    public AgencyResponse registerNewAgency(AgencyKey agencyKey, AgencyReceived receivedData) {
        String originalMessage = agencyDomainService.extractOriginalMessage(agencyKey, receivedData.getEncryptData());

        System.out.println("Original Message: " + originalMessage);

        RegisterInfo registerInfo = Utils.jsonStringToObject(originalMessage, RegisterInfo.class);

        System.out.println("RegisterInfo: " + registerInfo);

        Set<String> missingFields = validateNotNullFields(registerInfo);
        if (!missingFields.isEmpty()) {
            String missingField = String.join(", ", missingFields);
            System.out.println("missingField : " + missingField);
            return new AgencyResponse(EnumResultCode.NoSuchFieldError.getCode(), missingField + EnumResultCode.NoSuchFieldError.getMessage());
        }

        Agency agency = registerInfo.toAgency();
        save(agency);
        String targetData = agencyDomainService.generateTargetDate(agency, REGISTER_TYPE);
        String messageType = EnumAgency.getMsgType(agencyKey.keyString(), REGISTER_TYPE);

        String encryptedData = agencyDomainService.encryptData(agencyKey, targetData);
        String hmac = agencyDomainService.generateHmac(agencyKey, targetData);

        return new AgencyResponse(encryptedData, hmac, messageType);
    }

    private Set<String> validateNotNullFields(Object object) {
        Set<ConstraintViolation<Object>> violations = validator.validate(object);
        System.out.println("violations : " + violations);
        return violations.stream()
                .filter(violation -> violation.getConstraintDescriptor().getAnnotation() instanceof NotNull)
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }


    @Transactional
    public AgencyResponse cancelAgency(AgencyKey agencyKey, AgencyReceived receivedData) {
        String originalMessage = agencyDomainService.extractOriginalMessage(agencyKey, receivedData.getEncryptData());
        CancelInfo cancelInfo = Utils.jsonStringToObject(originalMessage, CancelInfo.class);
        Agency agency = getAgency(SiteId.of(cancelInfo.getSiteId()));
        String targetDate = agencyDomainService.generateTargetDate(agency, CANCEL_TYPE);

        String encryptedData = agencyDomainService.encryptData(agencyKey, targetDate);
        String hmac = agencyDomainService.generateHmac(agencyKey, targetDate);

        return new AgencyResponse(encryptedData, hmac, CANCEL_TYPE);
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
    public void save(Agency agency) {
        agencyRepository.add(agency);
    }
}
