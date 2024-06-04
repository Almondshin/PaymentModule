package com.modules.link.service.agency;

import com.modules.link.controller.container.AgencyReceived;
import com.modules.link.domain.agency.*;
import com.modules.link.domain.agency.service.AgencyDomainService;
import com.modules.link.enums.EnumAgency;
import com.modules.link.enums.EnumResultCode;
import com.modules.link.infrastructure.Notifier;
import com.modules.link.service.agency.dto.AgencyDtos.AgencyResponse;
import com.modules.link.service.agency.dto.AgencyDtos.CancelInfo;
import com.modules.link.service.agency.dto.AgencyDtos.RegisterInfo;
import com.modules.link.service.agency.dto.AgencyDtos.StatusInfo;
import com.modules.link.service.notify.dto.notifyDtos;
import com.modules.link.service.notify.dto.notifyDtos.RegisterNotification;
import com.modules.link.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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
    private final Notifier notifier;
    private final Validator validator;


    public AgencyService(AgencyRepository agencyRepository, AgencyDomainService agencyDomainService, Notifier notifier, Validator validator) {
        this.agencyRepository = agencyRepository;
        this.agencyDomainService = agencyDomainService;
        this.notifier = notifier;
        this.validator = validator;
    }

    public Optional<AgencyResponse> validateHmacAndMsgType(AgencyKey agencyKey, String msgType, String encryptData, String verifyInfo) {
        return agencyKey.validateHmacAndMsgType(msgType, encryptData, verifyInfo).map(AgencyResponse::new);
    }

    @Transactional
    public AgencyResponse getSiteStatus(AgencyKey agencyKey, AgencyReceived receivedData) {
        String originalMessage = agencyDomainService.extractOriginalMessage(agencyKey, receivedData.getEncryptData());
        StatusInfo statusInfo = Utils.jsonStringToObject(originalMessage, StatusInfo.class);
        Agency agency = getAgency(statusInfo.getSiteId());
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
        RegisterInfo registerInfo = Utils.jsonStringToObject(originalMessage, RegisterInfo.class);

        Set<String> missingFields = validateNotNullFields(registerInfo);
        if (!missingFields.isEmpty()) {
            String missingField = String.join(", ", missingFields);
            return new AgencyResponse(EnumResultCode.NoSuchFieldError.getCode(), missingField + EnumResultCode.NoSuchFieldError.getMessage());
        }

        if (getSite(registerInfo.getSiteId()) != null || getAgency(registerInfo.getSiteId()) != null) {
            return new AgencyResponse(EnumResultCode.DuplicateMember.getCode(), EnumResultCode.DuplicateMember.getMessage());
        }
        Agency agency = registerInfo.toAgency();
        save(agency);

        String registerMessage = new RegisterNotification(registerInfo.getSiteId(), registerInfo.getAgencyId(), registerInfo.getSiteName()).makeNotification();
        notifier.sendNotification(profileSpecificAdminUrl + "/clientManagement/agency/register/email", registerMessage);
        return new AgencyResponse();
    }

    private Set<String> validateNotNullFields(Object object) {
        Set<ConstraintViolation<Object>> violations = validator.validate(object);
        return violations.stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }


    @Transactional
    public AgencyResponse cancelAgency(AgencyKey agencyKey, AgencyReceived receivedData) {
        String originalMessage = agencyDomainService.extractOriginalMessage(agencyKey, receivedData.getEncryptData());
        CancelInfo cancelInfo = Utils.jsonStringToObject(originalMessage, CancelInfo.class);
        String cancelMessage = new notifyDtos.CancelNotification(cancelInfo.getSiteId(), cancelInfo.getAgencyId()).makeNotification();
        notifier.sendNotification(profileSpecificAdminUrl + "/clientManagement/agency/register/email", cancelMessage);
        return new AgencyResponse();
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
