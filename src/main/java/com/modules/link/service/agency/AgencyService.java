package com.modules.link.service.agency;

import com.modules.link.controller.container.AgencyReceived;
import com.modules.link.domain.agency.*;
import com.modules.link.domain.agency.service.AgencyDomainService;
import com.modules.link.enums.EnumAgency;
import com.modules.link.enums.EnumResultCode;
import com.modules.link.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.modules.link.service.agency.AgencyDtos.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Objects;
import java.util.Set;

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

    public AgencyService(AgencyRepository agencyRepository, AgencyDomainService agencyDomainService) {
        this.agencyRepository = agencyRepository;
        this.agencyDomainService = agencyDomainService;
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

        System.out.println("new AgencyResponse(encryptedData, hmac, messageType) : " + new AgencyResponse(encryptedData, hmac, messageType));

        return new AgencyResponse(encryptedData, hmac, messageType);
    }

    @Transactional
    public AgencyResponse registerNewAgency(AgencyKey agencyKey, AgencyReceived receivedData) {
        String originalMessage = agencyDomainService.extractOriginalMessage(agencyKey, receivedData.getEncryptData());
        RegisterInfo registerInfo = Utils.jsonStringToObject(originalMessage, RegisterInfo.class);
        Agency agency = registerInfo.toAgency();
        save(agency);
        String targetData = agencyDomainService.generateTargetDate(agency, REGISTER_TYPE);
        String messageType = EnumAgency.getMsgType(agencyKey.keyString(), REGISTER_TYPE);

        String encryptedData = agencyDomainService.encryptData(agencyKey, targetData);
        String hmac = agencyDomainService.generateHmac(agencyKey, targetData);

        return new AgencyResponse(encryptedData, hmac, messageType);
    }


    @Transactional
    public AgencyResponse cancelAgency(AgencyKey agencyKey, AgencyReceived receivedData) {
        String originalMessage = agencyDomainService.extractOriginalMessage(agencyKey, receivedData.getEncryptData());
        CancelInfo cancelInfo = Utils.jsonStringToObject(originalMessage, CancelInfo.class);
        Agency agency = getAgency(SiteId.of(cancelInfo.getSiteId()));
        String targetDate = agencyDomainService.generateTargetDate(agency, CANCEL_TYPE);

        //TODO
//        notiService.sendNotification("http://example.com/clientManagement/agency/cancel", targetDate);
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
