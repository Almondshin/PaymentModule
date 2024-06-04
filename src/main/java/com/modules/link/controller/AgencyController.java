package com.modules.link.controller;

import com.modules.link.controller.container.AgencyReceived;
import com.modules.link.domain.agency.AgencyId;
import com.modules.link.domain.agency.AgencyKey;
import com.modules.link.service.agency.dto.AgencyDtos.AgencyResponse;
import com.modules.link.service.agency.AgencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = {"/agency", "/"})
@RequiredArgsConstructor
public class AgencyController {

    private final AgencyService agencyService;

    @PostMapping("/getSiteStatus")
    public ResponseEntity<AgencyResponse> getStatus(@RequestBody AgencyReceived receivedData) {
        AgencyId agencyId = AgencyId.of(receivedData.getAgencyId());
        AgencyKey agencyKey = agencyService.getAgencyKey(agencyId);
        return ResponseEntity.ok(
                agencyService.validateHmacAndMsgType(
                                agencyKey,
                                receivedData.getMsgType(),
                                receivedData.getEncryptData(),
                                receivedData.getVerifyInfo())
                        .orElseGet(() -> agencyService.getSiteStatus(agencyKey, receivedData)));
    }

    @PostMapping("/regSiteInfo")
    public ResponseEntity<AgencyResponse> save(@RequestBody AgencyReceived receivedData) {
        AgencyId agencyId = AgencyId.of(receivedData.getAgencyId());
        AgencyKey agencyKey = agencyService.getAgencyKey(agencyId);
        return ResponseEntity.ok(
                agencyService.validateHmacAndMsgType(
                                agencyKey,
                                receivedData.getMsgType(),
                                receivedData.getEncryptData(),
                                receivedData.getVerifyInfo())
                        .orElseGet(() -> agencyService.registerNewAgency(agencyKey, receivedData)));
    }

    @PostMapping("/cancelSiteInfo")
    public ResponseEntity<AgencyResponse> cancel(@RequestBody AgencyReceived receivedData) {
        AgencyId agencyId = AgencyId.of(receivedData.getAgencyId());
        AgencyKey agencyKey = agencyService.getAgencyKey(agencyId);
        return ResponseEntity.ok(
                agencyService.validateHmacAndMsgType(
                                agencyKey,
                                receivedData.getMsgType(),
                                receivedData.getEncryptData(),
                                receivedData.getVerifyInfo())
                        .orElseGet(() -> agencyService.cancelAgency(agencyKey, receivedData)));
    }
}

