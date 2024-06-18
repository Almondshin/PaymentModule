package com.modules.link.controller;

import com.modules.link.controller.container.AgencyReceived;
import com.modules.link.controller.container.AgencyResponse;
import com.modules.link.controller.dto.AgencyDtos.CancelInfo;
import com.modules.link.controller.dto.AgencyDtos.RegisterInfo;
import com.modules.link.controller.dto.AgencyDtos.StatusInfo;
import com.modules.link.controller.dto.notifyDtos.RegisterNotification;
import com.modules.link.controller.mapper.AgencyMapper;
import com.modules.link.domain.agency.AgencyId;
import com.modules.link.domain.agency.AgencyKey;
import com.modules.link.domain.agency.SiteId;
import com.modules.link.domain.validate.ValidateInfo;
import com.modules.link.enums.EnumAgency;
import com.modules.link.infrastructure.notification.NotificationSender;
import com.modules.link.service.agency.AgencyService;
import com.modules.link.service.validate.ValidateService;
import com.modules.link.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = {"/agency", "/"})
@RequiredArgsConstructor
public class AgencyController {

    private static final String SITE_INFO = "info";
    private static final String REGISTER_API = "/clientManagement/agency/register/email";
    private static final String CANCEL_API = "/clientManagement/agency/cancel";

    private final AgencyService agencyService;
    private final ValidateService validateService;
    private final NotificationSender notificationSender;

    @Value("${external.admin.url}")
    private String profileSpecificAdminUrl;

    @PostMapping("/getSiteStatus")
    public ResponseEntity<AgencyResponse> getStatus(@RequestBody AgencyReceived receivedData) {
        AgencyId agencyId = AgencyId.of(receivedData.getAgencyId());
        AgencyKey agencyKey = agencyService.getAgencyKey(agencyId);
        String keyString = agencyKey.keyString();
        String key = agencyKey.getKey();
        String iv = agencyKey.getIv();
        ValidateInfo validateInfo = ValidateInfo.builder()
                .messageType(receivedData.getMsgType())
                .encryptDate(receivedData.getEncryptData())
                .verifyInfo(receivedData.getVerifyInfo())
                .build();
        validateService.validateHmacAndMsgType(validateInfo, keyString, key, iv);

        String originalMessage = validateService.originalMessage(validateInfo, key, iv);
        StatusInfo statusInfo = Utils.jsonStringToObject(originalMessage, StatusInfo.class);
        validateService.isSiteIdStartWithInitial(agencyId.toString(), statusInfo.getSiteId().toString());

        String targetData = agencyService.generateSiteStatusData(statusInfo.getSiteId());
        return ResponseEntity.ok(
                AgencyResponse.builder()
                        .encryptData(validateService.encryptData(targetData, key, iv))
                        .verifyInfo(validateService.hmacSHA256(targetData, keyString))
                        .messageType(EnumAgency.getMsgType(agencyKey.keyString(), SITE_INFO))
                        .build());
    }


    @PostMapping("/regSiteInfo")
    @Validated
    public ResponseEntity<AgencyResponse> save(@RequestBody AgencyReceived receivedData) {
        AgencyId agencyId = AgencyId.of(receivedData.getAgencyId());
        AgencyKey agencyKey = agencyService.getAgencyKey(agencyId);
        String key = agencyKey.getKey();
        String iv = agencyKey.getIv();
        ValidateInfo validateInfo = ValidateInfo.builder()
                .messageType(receivedData.getMsgType())
                .encryptDate(receivedData.getEncryptData())
                .verifyInfo(receivedData.getVerifyInfo())
                .build();
        validateService.validateHmacAndMsgType(validateInfo, agencyKey.keyString(), key, iv);

        String originalMessage = validateService.originalMessage(validateInfo, key, iv);
        RegisterInfo registerInfo = Utils.jsonStringToObject(originalMessage, RegisterInfo.class);
        validateService.isSiteIdStartWithInitial(agencyId.toString(), registerInfo.getSiteId().toString());

        agencyService.save(new SiteId(registerInfo.getSiteId().toString()), AgencyMapper.toAgency(registerInfo));

        String registerMessage = new RegisterNotification(registerInfo.getSiteId(), registerInfo.getAgencyId(), registerInfo.getSiteName()).makeNotification();
        notificationSender.sendNotification(profileSpecificAdminUrl + REGISTER_API, registerMessage);
        return ResponseEntity.ok(new AgencyResponse());
    }

    @PostMapping("/cancelSiteInfo")
    public ResponseEntity<AgencyResponse> cancel(@RequestBody AgencyReceived receivedData) {
        AgencyId agencyId = AgencyId.of(receivedData.getAgencyId());
        AgencyKey agencyKey = agencyService.getAgencyKey(agencyId);
        String key = agencyKey.getKey();
        String iv = agencyKey.getIv();
        ValidateInfo validateInfo = ValidateInfo.builder()
                .messageType(receivedData.getMsgType())
                .encryptDate(receivedData.getEncryptData())
                .verifyInfo(receivedData.getVerifyInfo())
                .build();
        validateService.validateHmacAndMsgType(validateInfo, agencyKey.keyString(), key, iv);

        String originalMessage = validateService.originalMessage(validateInfo, key, iv);
        CancelInfo cancelInfo = Utils.jsonStringToObject(originalMessage, CancelInfo.class);
        validateService.isSiteIdStartWithInitial(agencyId.toString(), cancelInfo.getSiteId().toString());

        String targetData = agencyService.generateCancelData(cancelInfo.getSiteId());
        notificationSender.sendNotification(profileSpecificAdminUrl + CANCEL_API, targetData);
        return ResponseEntity.ok(new AgencyResponse());
    }

}

