package com.modules.payment.adapter.in.web;

import com.modules.payment.application.exceptions.enums.EnumResultCode;
import com.modules.payment.application.port.in.AgencyUseCase;
import com.modules.payment.application.port.in.EncryptUseCase;
import com.modules.payment.application.port.in.NotiUseCase;
import com.modules.payment.application.utils.Utils;
import com.modules.payment.domain.Agency;
import com.modules.payment.domain.ResponseManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(value = {"/agency", "/"})
public class AgencyController {
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
     * @param agency the agency
     * @return 결과코드 및 메세지와 SHA256 암호화 된 정보, 검증 정보 반환
     */
    @PostMapping("/getSiteStatus")
    public ResponseEntity<?> getSiteStatus(@RequestBody Agency agency) {
        Optional<Agency> info = agencyUseCase.getAgencyInfo(agency);
        Map<String, String> encryptMapData = new HashMap<>();

        if (info.isPresent()) {
            Agency agencyInfo = info.get();
            encryptMapData = agencyInfo.generateMapData("encrypt");
        }

        String keyString = agency.keyString();
        Map<String, String> keyIv = encryptUseCase.getKeyIv(keyString);
        String agencyKey = keyIv.get("agencyKey");
        String agencyIv = keyIv.get("agencyIv");

        boolean isVerifiedHmac = agency.isVerifiedHmac(agencyKey, agencyIv);
        boolean isVerifiedMsgType = agency.isVerifiedMessageType("status");

        try {
            String encryptStringData = Utils.mapToJSONString(encryptMapData);
            String encryptData = encryptUseCase.encryptData(Objects.requireNonNull(encryptStringData), keyIv);
            String verifyInfo = encryptUseCase.hmacSHA256(Objects.requireNonNull(encryptStringData), keyString);

            verifiedHmacAndType(isVerifiedHmac, isVerifiedMsgType);
            ResponseManager manager = new ResponseManager("SiteInfo", encryptData, verifyInfo);
            return ResponseEntity.ok(manager);
        } catch (IllegalStateException e) {
            return ResponseEntity.ok(new ResponseManager("9999", e.getMessage()));
        }
    }

    /**
     * Reg site info response entity.
     *
     * @param agency the agency
     * @return the response entity
     */
    @PostMapping("/regSiteInfo")
    public ResponseEntity<?> regSiteInfo(@RequestBody Agency agency) {
        String keyString = agency.keyString();
        Map<String, String> keyIv = encryptUseCase.getKeyIv(keyString);
        String agencyKey = keyIv.get("agencyKey");
        String agencyIv = keyIv.get("agencyIv");

        boolean isVerifiedHmac = agency.isVerifiedHmac(agencyKey, agencyIv);
        boolean isVerifiedMsgType = agency.isVerifiedMessageType("reg");

        Agency registerInfo = agency.registerAgencyInfo(agencyKey, agencyIv);
        try {
            verifiedHmacAndType(isVerifiedHmac, isVerifiedMsgType);
            registerInfo.validateRequiredValues();

            agencyUseCase.registerAgency(registerInfo);
            ResponseManager manager = new ResponseManager(EnumResultCode.SUCCESS.getCode(), EnumResultCode.SUCCESS.getValue());
            return ResponseEntity.ok(manager);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.ok(new ResponseManager("9999", e.getMessage()));
        } finally {
            String notificationData = Utils.mapToJSONString(agency.generateMapData("AdminNotification"));
            notiUseCase.sendNotification(profileSpecificAdminUrl + "/clientManagement/agency/register/email", notificationData);
        }
    }

    /**
     * Cancel site info response entity.
     *
     * @param agency the agency
     * @return the response entity
     */
    @PostMapping("/cancelSiteInfo")
    public ResponseEntity<?> cancelSiteInfo(@RequestBody Agency agency) {
        String keyString = agency.keyString();
        Map<String, String> keyIv = encryptUseCase.getKeyIv(keyString);
        String agencyKey = keyIv.get("agencyKey");
        String agencyIv = keyIv.get("agencyIv");

        boolean isVerifiedHmac = agency.isVerifiedHmac(agencyKey, agencyIv);
        boolean isVerifiedMsgType = agency.isVerifiedMessageType("cancel");
        try {
            verifiedHmacAndType(isVerifiedHmac, isVerifiedMsgType);
            ResponseManager manager = new ResponseManager(EnumResultCode.SUCCESS.getCode(), EnumResultCode.SUCCESS.getValue());
            return ResponseEntity.ok(manager);
        } catch (IllegalStateException e) {
            return ResponseEntity.ok(new ResponseManager("9999", e.getMessage()));
        } finally {
            String notificationData = Utils.mapToJSONString(agency.generateMapData("AdminNotification"));
            notiUseCase.sendNotification(profileSpecificAdminUrl + "/clientManagement/agency/cancel", notificationData);
        }
    }


    private void verifiedHmacAndType(boolean isVerifiedHmac, boolean isVerifiedMsgType) {
        if (!isVerifiedHmac) {
            throw new IllegalStateException("HMAC 검증에 실패하였습니다.");
        }
        if (!isVerifiedMsgType) {
            throw new IllegalStateException("MsgType 검증이 실패하였습니다.");
        }
    }

}
