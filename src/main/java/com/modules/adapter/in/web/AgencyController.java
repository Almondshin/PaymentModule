package com.modules.adapter.in.web;

import com.modules.adapter.in.models.ClientDataContainer;
import com.modules.adapter.in.models.ResponseManager;
import com.modules.application.exceptions.enums.EnumResultCode;
import com.modules.application.port.in.AgencyUseCase;
import com.modules.application.port.in.EncryptUseCase;
import com.modules.application.port.in.NotiUseCase;
import com.modules.application.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


//TODO
// 각 메서드 마다 logging 추가
@Slf4j
@RestController
@RequestMapping(value = {"/agency", "/"})
public class AgencyController {
    private final AgencyUseCase agencyUseCase;
    private final EncryptUseCase encryptUseCase;
    private final NotiUseCase notiUseCase;

    @Value("${external.admin.url}")
    private String profileSpecificAdminUrl;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AgencyController(AgencyUseCase agencyUseCase, EncryptUseCase encryptUseCase, NotiUseCase notiUseCase) {
        this.agencyUseCase = agencyUseCase;
        this.encryptUseCase = encryptUseCase;
        this.notiUseCase = notiUseCase;
    }

    @PostMapping("/getSiteStatus")
    public ResponseEntity<?> getSiteStatus(@RequestBody ClientDataContainer clientDataContainer) {
        Optional<ClientDataContainer> info = agencyUseCase.getAgencyInfo(clientDataContainer);
        Map<String, String> encryptMapData = new HashMap<>();

        if (info.isPresent()) {
            ClientDataContainer clientInfo = info.get();
            encryptMapData = clientInfo.makeEncryptMapData();
        }

        String keyString = clientDataContainer.keyString();
        Map<String, String> keyIv = encryptUseCase.getKeyIv(keyString);
        String agencyKey = keyIv.get("agencyKey");
        String agencyIv = keyIv.get("agencyIv");

        boolean isVerifiedHmac = clientDataContainer.isVerifiedHmac(agencyKey, agencyIv);
        boolean isVerifiedMsgType = clientDataContainer.isVerifiedMessageType("status");

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

    @PostMapping("/regSiteInfo")
    public ResponseEntity<?> regSiteInfo(@RequestBody ClientDataContainer clientDataContainer) {
        String keyString = clientDataContainer.keyString();
        Map<String, String> keyIv = encryptUseCase.getKeyIv(keyString);
        String agencyKey = keyIv.get("agencyKey");
        String agencyIv = keyIv.get("agencyIv");

        boolean isVerifiedHmac = clientDataContainer.isVerifiedHmac(agencyKey, agencyIv);
        boolean isVerifiedMsgType = clientDataContainer.isVerifiedMessageType("reg");

        ClientDataContainer registerInfo = clientDataContainer.registerAgencyInfo(agencyKey, agencyIv);
        try {
            verifiedHmacAndType(isVerifiedHmac, isVerifiedMsgType);
            validateRequiredValues(registerInfo);

            agencyUseCase.registerAgency(registerInfo);
            ResponseManager manager = new ResponseManager(EnumResultCode.SUCCESS.getCode(), EnumResultCode.SUCCESS.getValue());
            return ResponseEntity.ok(manager);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.ok(new ResponseManager("9999", e.getMessage()));
        } finally {
            String notificationData = Utils.mapToJSONString(clientDataContainer.notificationData());
            notiUseCase.sendNotification(profileSpecificAdminUrl + "/clientManagement/agency/register/email", notificationData);
        }
    }

    @PostMapping("/cancelSiteInfo")
    public ResponseEntity<?> cancelSiteInfo(@RequestBody ClientDataContainer clientDataContainer) {
        String keyString = clientDataContainer.keyString();
        Map<String, String> keyIv = encryptUseCase.getKeyIv(keyString);
        String agencyKey = keyIv.get("agencyKey");
        String agencyIv = keyIv.get("agencyIv");

        boolean isVerifiedHmac = clientDataContainer.isVerifiedHmac(agencyKey, agencyIv);
        boolean isVerifiedMsgType = clientDataContainer.isVerifiedMessageType("cancel");
        try {
            verifiedHmacAndType(isVerifiedHmac, isVerifiedMsgType);
            ResponseManager manager = new ResponseManager(EnumResultCode.SUCCESS.getCode(), EnumResultCode.SUCCESS.getValue());
            return ResponseEntity.ok(manager);
        } catch (IllegalStateException e) {
            return ResponseEntity.ok(new ResponseManager("9999", e.getMessage()));
        } finally {
            String notificationData = Utils.mapToJSONString(clientDataContainer.notificationData());
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


    public void validateRequiredValues(ClientDataContainer clientDataContainer) {
        String field = clientDataContainer.checkRequiredFields(clientDataContainer);
        if (field != null && !field.isEmpty()) {
            throw new IllegalArgumentException(field + " 필드가 비어 있습니다.");
        }
    }
}
