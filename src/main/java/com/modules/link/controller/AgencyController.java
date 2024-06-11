package com.modules.link.controller;

import com.modules.link.controller.container.AgencyReceived;
import com.modules.link.controller.dto.AgencyDtos.*;
import com.modules.link.controller.dto.notifyDtos.*;
import com.modules.link.controller.response.AgencyResponse;
import com.modules.link.domain.agency.Agency;
import com.modules.link.domain.agency.AgencyId;
import com.modules.link.domain.agency.AgencyKey;
import com.modules.link.domain.validate.ValidateInfo;
import com.modules.link.enums.EnumAgency;
import com.modules.link.enums.EnumResultCode;
import com.modules.link.infrastructure.Notifier;
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

import javax.persistence.EntityExistsException;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.AbstractMap.SimpleEntry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = {"/agency", "/"})
@RequiredArgsConstructor
public class AgencyController {

    private static final String SITE_INFO = "info";
    private static final String REGISTER_API = "/clientManagement/agency/register/email";
    private static final String CANCEL_API = "/clientManagement/agency/cancel";

    private final AgencyService agencyService;
    private final ValidateService validateService;
    private final Validator validator;
    private final Notifier notifier;

    @Value("${external.admin.url}")
    private String profileSpecificAdminUrl;

    // getStatus라는 API를 요청하면
    // client로부터 받은 암호화된 값을 복호화 및 검증하고 [validate 도메인]
    // 검증결과에따라 다음단계 진행
    // 검증된 client로 부터 받은 암호화된 값을 복호화하고
    // 복호화 된 값을 하나의 객체로 변환하고 [Adapter-Dto]
    // API에 반환하기 위한 데이터를 조형한다. [Adapter-AgencyResponse]
    @PostMapping("/getSiteStatus")
    public ResponseEntity<AgencyResponse> getStatus(@RequestBody AgencyReceived receivedData) {
        AgencyId agencyId = AgencyId.of(receivedData.getAgencyId());
        AgencyKey agencyKey = agencyService.getAgencyKey(agencyId);
        ValidateInfo validateInfo = ValidateInfo.builder()
                .messageType(receivedData.getMsgType())
                .encryptDate(receivedData.getEncryptData())
                .verifyInfo(receivedData.getVerifyInfo())
                .build();
        Optional<EnumResultCode> validationResult = validateService.validateHmacAndMsgType(validateInfo, agencyKey);
        if (validationResult.isPresent() && !validationResult.get().equals(EnumResultCode.SUCCESS)) {
            return ResponseEntity.ok(AgencyResponse.toAgencyResponse(validationResult.get()));
        }
        String originalMessage = validateService.originalMessage(validateInfo, agencyKey);
        StatusInfo statusInfo = Utils.jsonStringToObject(originalMessage, StatusInfo.class);
        String targetData = agencyService.generateSiteStatusData(statusInfo.getSiteId());
        return ResponseEntity.ok(
                AgencyResponse.builder()
                        .encryptData(validateService.encryptData(targetData, agencyKey))
                        .verifyInfo(validateService.hmacSHA256(targetData, agencyKey))
                        .messageType(EnumAgency.getMsgType(agencyKey.keyString(), SITE_INFO))
                        .build());
    }


    @PostMapping("/regSiteInfo")
    @Validated
    public ResponseEntity<AgencyResponse> save(@RequestBody AgencyReceived receivedData) {
        AgencyId agencyId = AgencyId.of(receivedData.getAgencyId());
        AgencyKey agencyKey = agencyService.getAgencyKey(agencyId);
        ValidateInfo validateInfo = ValidateInfo.builder()
                .messageType(receivedData.getMsgType())
                .encryptDate(receivedData.getEncryptData())
                .verifyInfo(receivedData.getVerifyInfo())
                .build();
        Optional<EnumResultCode> validationResult = validateService.validateHmacAndMsgType(validateInfo, agencyKey);
        if (validationResult.isPresent() && !validationResult.get().equals(EnumResultCode.SUCCESS)) {
            return ResponseEntity.ok(AgencyResponse.toAgencyResponse(validationResult.get()));
        }
        String originalMessage = validateService.originalMessage(validateInfo, agencyKey);
        RegisterInfo registerInfo = Utils.jsonStringToObject(originalMessage, RegisterInfo.class);
        Set<String> missingFields = validateNotNullFields(registerInfo);
        if (!missingFields.isEmpty()) {
            String missingField = String.join(", ", missingFields);
            return ResponseEntity.ok(new AgencyResponse(
                    EnumResultCode.NoSuchFieldError.getCode(),
                    missingField + EnumResultCode.NoSuchFieldError.getMessage()));
        }

        try {
            agencyService.save(registerInfo.toAgency());
        } catch (EntityExistsException e) {
            return ResponseEntity.ok(new AgencyResponse(EnumResultCode.DuplicateMember.getCode(), e.getMessage()));
        }
        String registerMessage = new RegisterNotification(registerInfo.getSiteId(), registerInfo.getAgencyId(), registerInfo.getSiteName()).makeNotification();
        notifier.sendNotification(profileSpecificAdminUrl + REGISTER_API, registerMessage);
        return ResponseEntity.ok(new AgencyResponse());
    }

    private Set<String> validateNotNullFields(RegisterInfo registerInfo) {
        Set<ConstraintViolation<RegisterInfo>> violations = validator.validate(registerInfo);
        return violations.stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }


    @PostMapping("/cancelSiteInfo")
    public ResponseEntity<AgencyResponse> cancel(@RequestBody AgencyReceived receivedData) {
        AgencyId agencyId = AgencyId.of(receivedData.getAgencyId());
        AgencyKey agencyKey = agencyService.getAgencyKey(agencyId);
        ValidateInfo validateInfo = ValidateInfo.builder()
                .messageType(receivedData.getMsgType())
                .encryptDate(receivedData.getEncryptData())
                .verifyInfo(receivedData.getVerifyInfo())
                .build();
        Optional<EnumResultCode> validationResult = validateService.validateHmacAndMsgType(validateInfo, agencyKey);
        if (validationResult.isPresent() && !validationResult.get().equals(EnumResultCode.SUCCESS)) {
            return ResponseEntity.ok(AgencyResponse.toAgencyResponse(validationResult.get()));
        }
        String originalMessage = validateService.originalMessage(validateInfo, agencyKey);
        CancelInfo cancelInfo = Utils.jsonStringToObject(originalMessage, CancelInfo.class);
        agencyService.getAgency(cancelInfo.getSiteId());
        String cancelMessage = new CancelNotification(cancelInfo.getSiteId(), cancelInfo.getAgencyId()).makeNotification();
        notifier.sendNotification(profileSpecificAdminUrl + CANCEL_API, cancelMessage);
        return ResponseEntity.ok(new AgencyResponse());
    }

}

