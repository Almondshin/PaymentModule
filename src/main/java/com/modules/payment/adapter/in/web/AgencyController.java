package com.modules.payment.adapter.in.web;

import com.modules.payment.application.exceptions.enums.EnumResultCode;
import com.modules.payment.application.port.in.AgencyUseCase;
import com.modules.payment.application.port.in.EncryptUseCase;
import com.modules.payment.application.port.in.NotiUseCase;
import com.modules.payment.domain.Agency;
import com.modules.payment.domain.AgencyInfoKey;
import com.modules.payment.domain.ResponseManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping(value = {"/agency", "/"})
public class AgencyController {

    private static final String ERROR_CODE = "9999";
    private static final String AGENCY_MSG_TYPE = "SiteInfo";
    private static final String TYPE_ENCRYPT = "encrypt";
    private static final String ADMIN_NOTIFICATION = "AdminNotification";

    private final AgencyUseCase agencyUseCase;
    private final EncryptUseCase encryptUseCase;
    private final NotiUseCase notiUseCase;


    @Value("${external.admin.url}")
    private String profileSpecificAdminUrl;

    public AgencyController(AgencyUseCase agencyUseCase, EncryptUseCase encryptUseCase, NotiUseCase notiUseCase) {
        this.agencyUseCase = agencyUseCase;
        this.encryptUseCase = encryptUseCase;
        this.notiUseCase = notiUseCase;
    }

    /**
     * 사이트 상태 획득 API
     *
     * @param agency 필수 값 : agencyId, siteId
     * @return 결과 코드 및 메세지와 SHA256 암호화 된 정보, 검증 정보 반환
     */
    @PostMapping("/getSiteStatus")
    public ResponseEntity<?> getSiteStatus(@RequestBody Agency agency) {
        try {
            verifyAgency(agency);
            Optional<Agency> agencyOptional = agencyUseCase.getAgencyInfo(agency);
            Agency agencyInfo = agencyOptional.orElseThrow(() -> new IllegalStateException("Agency information not found"));
            String encryptStringData = agencyInfo.generateEncryptData(TYPE_ENCRYPT);
            AgencyInfoKey keyIv = encryptUseCase.getKeyIv(agencyInfo.keyString());
            String encryptData = encryptUseCase.encryptData(encryptStringData, keyIv);
            String verifyInfo = encryptUseCase.hmacSHA256(encryptStringData, agencyInfo.keyString());
            ResponseManager manager = new ResponseManager(AGENCY_MSG_TYPE, encryptData, verifyInfo);
            return ResponseEntity.ok(manager);
        } catch (IllegalStateException e) {
            return ResponseEntity.ok(new ResponseManager(ERROR_CODE, e.getMessage()));
        }
    }

    /**
     * 이용기관(사이트) 등록 요청 API
     *
     * @param agency the agency
     * @return 결과 코드 및 메세지
     * @finally 관리자에게 등록 메일 요청(agencyId, siteId, siteName)
     */
    @PostMapping("/regSiteInfo")
    public ResponseEntity<?> regSiteInfo(@RequestBody Agency agency) {
        try {
            verifyAgency(agency);
            String keyString = agency.keyString();
            AgencyInfoKey keyIv = encryptUseCase.getKeyIv(keyString);
            Agency registerInfo = agency.registerAgencyInfo(keyIv);
            registerInfo.validateRequiredValues();
            agencyUseCase.registerAgency(registerInfo);
            ResponseManager manager = new ResponseManager(EnumResultCode.SUCCESS.getCode(), EnumResultCode.SUCCESS.getValue());
            notiUseCase.sendNotification(profileSpecificAdminUrl + "/clientManagement/agency/register/email", agency.generateNotificationData(ADMIN_NOTIFICATION));
            return ResponseEntity.ok(manager);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.ok(new ResponseManager(ERROR_CODE, e.getMessage()));
        }
    }

    /**
     * 이용기관(사이트) 해지 요청 API
     *
     * @param agency the agency
     * @return 결과 코드 및 메세지
     * @finally 관리자에게 해지 메일 요청(agencyId, siteId, siteName)
     */
    @PostMapping("/cancelSiteInfo")
    public ResponseEntity<?> cancelSiteInfo(@RequestBody Agency agency) {
        try {
            verifyAgency(agency);
            ResponseManager manager = new ResponseManager(EnumResultCode.SUCCESS.getCode(), EnumResultCode.SUCCESS.getValue());
            notiUseCase.sendNotification(profileSpecificAdminUrl + "/clientManagement/agency/cancel", agency.generateNotificationData(ADMIN_NOTIFICATION));
            return ResponseEntity.ok(manager);
        } catch (IllegalStateException e) {
            return ResponseEntity.ok(new ResponseManager(ERROR_CODE, e.getMessage()));
        }
    }

    private void verifyAgency(Agency agency) {
        String keyString = agency.keyString();
        AgencyInfoKey keyIv = encryptUseCase.getKeyIv(keyString);
        boolean isVerifiedHmac = agency.isVerifiedHmac(keyIv);
        boolean isVerifiedMsgType = agency.isVerifiedMessageType();

        if (!isVerifiedHmac) {
            throw new IllegalStateException("HMAC 검증에 실패하였습니다.");
        }
        if (!isVerifiedMsgType) {
            throw new IllegalStateException("MsgType 검증이 실패하였습니다.");
        }
    }
}
