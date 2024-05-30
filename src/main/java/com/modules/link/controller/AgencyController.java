package com.modules.link.controller;

import com.modules.link.controller.container.AgencyReceived;
import com.modules.link.domain.agency.AgencyId;
import com.modules.link.domain.agency.AgencyKey;
import com.modules.link.enums.EnumResultCode;
import com.modules.link.service.agency.AgencyDtos;
import com.modules.link.service.agency.AgencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;

@RestController
@RequestMapping(value = {"/agency", "/"})
@RequiredArgsConstructor
public class AgencyController {

    private final AgencyService agencyService;

    @PostMapping("/getSiteStatus")
    public ResponseEntity<AgencyDtos.AgencyResponse> getStatus(@RequestBody AgencyReceived receivedData) {
        AgencyId agencyId = AgencyId.of(receivedData.getAgencyId());
        AgencyKey agencyKey = agencyService.getAgencyKey(agencyId);
        return ResponseEntity.ok(
                agencyKey.validateHmacAndMsgType(
                                receivedData.getMsgType(),
                                receivedData.getEncryptData(),
                                receivedData.getVerifyInfo())
                        .map(AgencyDtos.AgencyResponse::new)
                        .orElseGet(() -> agencyService.getSiteStatus(agencyKey, receivedData)));
    }

    @PostMapping("/regSiteInfo")
    public ResponseEntity<?> save(@Valid @RequestBody AgencyReceived receivedData, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String missingField = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            return ResponseEntity.ok(new AgencyDtos.AgencyResponse(EnumResultCode.NoSuchFieldError.getCode(), missingField + EnumResultCode.NoSuchFieldError.getMessage()));
        }
        AgencyId agencyId = AgencyId.of(receivedData.getAgencyId());
        AgencyKey agencyKey = agencyService.getAgencyKey(agencyId);
        return ResponseEntity.ok(
                agencyKey.validateHmacAndMsgType(
                                receivedData.getMsgType(),
                                receivedData.getEncryptData(),
                                receivedData.getVerifyInfo())
                        .map(AgencyDtos.AgencyResponse::new)
                        .orElseGet(() -> agencyService.registerNewAgency(agencyKey, receivedData)));
    }

    @PostMapping("/cancelSiteInfo")
    public ResponseEntity<AgencyDtos.AgencyResponse> cancel(@RequestBody AgencyReceived receivedData) {
        AgencyId agencyId = AgencyId.of(receivedData.getAgencyId());
        AgencyKey agencyKey = agencyService.getAgencyKey(agencyId);
        return ResponseEntity.ok(
                agencyKey.validateHmacAndMsgType(
                                receivedData.getMsgType(),
                                receivedData.getEncryptData(),
                                receivedData.getVerifyInfo())
                        .map(AgencyDtos.AgencyResponse::new)
                        .orElseGet(() -> agencyService.cancelAgency(agencyKey, receivedData)));
    }
}
