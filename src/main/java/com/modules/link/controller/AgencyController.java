package com.modules.link.controller;

import com.modules.link.controller.container.AgencyReceived;
import com.modules.link.controller.container.AgencyResponse;
import com.modules.link.domain.agency.Agency;
import com.modules.link.domain.agency.AgencyKey;
import com.modules.link.domain.agency.SiteId;
import com.modules.link.enums.EnumAgency;
import com.modules.link.service.Notification.NotiService;
import com.modules.link.service.agency.AgencyService;
import com.modules.link.utils.Utils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@RestController("refactoringAgencyController")
@RequestMapping(value = {"/agency", "/"})
@RequiredArgsConstructor
public class AgencyController {

    private static final String STATUS_TYPE = "status";
    private static final String SITE_INFO = "info";
    private static final String REGISTER_TYPE = "reg";
    private static final String CANCEL_TYPE = "cancel";
    private final AgencyService agencyService;
    private final NotiService notiService;

    @Value("${external.admin.url}")
    private String profileSpecificAdminUrl;

    @PostMapping("/getSiteStatus")
    public ResponseEntity<AgencyResponse> getStatus(@RequestBody AgencyReceived receivedData) {
        AgencyKey agencyKey = agencyService.getAgencyKey(receivedData.getAgencyId());
        return ResponseEntity.ok(
                agencyKey.validateHmacAndMsgType(
                        receivedData.getMsgType(),
                        receivedData.getEncryptData(),
                        receivedData.getVerifyInfo())
                .map(AgencyResponse::new)
                .orElseGet(() -> getSiteStatus(agencyKey, receivedData)));
    }

    private AgencyResponse getSiteStatus(AgencyKey agencyKey, AgencyReceived receivedData) {
        StatusInfo statusInfo = Utils.jsonStringToObject(agencyKey.originalMessage(receivedData.getEncryptData()), StatusInfo.class);
        Agency agency = agencyService.getAgency(statusInfo.getSiteId());
        String targetDate = agency.makeVerifyAndEncryptData(STATUS_TYPE);
        String messageType = EnumAgency.getMsgType(agencyKey.getId(), SITE_INFO);
        return new AgencyResponse(agencyKey.encryptData(targetDate), agencyKey.hmacSHA256(targetDate), messageType);
    }

    @Getter
    public static class StatusInfo {
        private String agencyId;
        private SiteId siteId;
    }

    @PostMapping("/regSiteInfo")
    public ResponseEntity<?> save(@Valid @RequestBody AgencyReceived receivedData, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String missingField = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            throw new ResponseStatusException(HttpStatus.OK, missingField + " 필드가 비어 있습니다.");
        }
        AgencyKey agencyKey = agencyService.getAgencyKey(receivedData.getAgencyId());
        return ResponseEntity.ok(
                agencyKey.validateHmacAndMsgType(
                                receivedData.getMsgType(),
                                receivedData.getEncryptData(),
                                receivedData.getVerifyInfo())
                        .map(AgencyResponse::new)
                        .orElseGet(() -> registerNewAgency(agencyKey, receivedData)));
    }

    private AgencyResponse registerNewAgency(AgencyKey agencyKey, AgencyReceived receivedData) {
        RegisterInfo registerInfo = Utils.jsonStringToObject(agencyKey.originalMessage(receivedData.getEncryptData()), RegisterInfo.class);
        Agency agency = registerInfo.toAgency();
        agencyService.save(agency);
        String targetDate = agency.makeVerifyAndEncryptData(REGISTER_TYPE);
        String messageType = EnumAgency.getMsgType(agencyKey.getId(), REGISTER_TYPE);
        return new AgencyResponse(agencyKey.encryptData(targetDate), agencyKey.hmacSHA256(targetDate),messageType);
    }

    @Validated
    public static class RegisterInfo{
        @NotNull(message = "agencyId")
        private String agencyId;
        @NotNull(message = "siteId")
        private SiteId siteId;
        @NotNull(message = "siteName")
        private String siteName;
        @NotNull(message = "companyName")
        private String companyName;
        @NotNull(message = "businessType")
        private String businessType;
        @NotNull(message = "bizNumber")
        private String bizNumber;
        @NotNull(message = "ceoName")
        private String ceoName;
        @NotNull(message = "phoneNumber")
        private String phoneNumber;
        @NotNull(message = "address")
        private String address;
        @NotNull(message = "companySite")
        private String companySite;
        private String email;
        private String rateSel;
        private Date startDate;
        @NotNull(message = "settleManagerName")
        private String settleManagerName;
        @NotNull(message = "settleManagerPhoneNumber")
        private String settleManagerPhoneNumber;
        @NotNull(message = "settleManagerTelNumber")
        private String settleManagerTelNumber;
        @NotNull(message = "settleManagerEmail")
        private String settleManagerEmail;
        @NotNull(message = "serviceUseAgree")
        private String serviceUseAgree;

        public Agency toAgency() {
            return Agency.of(
                    this.siteId,
                    this.agencyId,
                    this.siteName,
                    this.companyName,
                    this.businessType,
                    this.bizNumber,
                    this.ceoName,
                    this.phoneNumber,
                    this.address,
                    this.companySite,
                    this.email,
                    this.rateSel,
                    this.startDate,
                    this.settleManagerName,
                    this.settleManagerPhoneNumber,
                    this.settleManagerTelNumber,
                    this.settleManagerEmail,
                    this.serviceUseAgree
            );
        }
    }

    @PostMapping("/cancelSiteInfo")
    public ResponseEntity<AgencyResponse> cancel(@RequestBody AgencyReceived receivedData) {
        AgencyKey agencyKey = agencyService.getAgencyKey(receivedData.getAgencyId());
        return ResponseEntity.ok(
                agencyKey.validateHmacAndMsgType(
                                receivedData.getMsgType(),
                                receivedData.getEncryptData(),
                                receivedData.getVerifyInfo())
                        .map(AgencyResponse::new)
                        .orElseGet(() -> cancelAgency(agencyKey, receivedData)));
    }

    private AgencyResponse cancelAgency(AgencyKey agencyKey, AgencyReceived receivedData) {
        CancelInfo cancelInfo = Utils.jsonStringToObject(agencyKey.originalMessage(receivedData.getEncryptData()), CancelInfo.class);
        Agency agency = agencyService.getAgency(cancelInfo.getSiteId());
        String targetDate = agency.makeVerifyAndEncryptData(CANCEL_TYPE);
        notiService.sendNotification(profileSpecificAdminUrl + "/clientManagement/agency/cancel", targetDate);
        return new AgencyResponse();
    }

    @Getter
    private static class CancelInfo{
        private String agencyId;
        private SiteId siteId;
    }

}
