package com.modules.adapter.in.web;

import com.modules.adapter.in.models.ClientDataContainer;
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
        String keyString = clientDataContainer.keyString();
        Map<String, String> keyIv = encryptUseCase.getKeyIv(keyString);
        byte[] plainBytes = encryptUseCase.decryptData(clientDataContainer, keyIv);
        String originalMessage = new String(plainBytes);

        Optional<ClientDataContainer> info = agencyUseCase.getAgencyInfo(clientDataContainer);
        Map<String, String> encryptMapData = new HashMap<>();

        if (info.isPresent()) {
            ClientDataContainer clientInfo = info.get();
            encryptMapData = clientInfo.makeEncryptMapData();
        }
        String encryptStringData = Utils.mapToJSONString(encryptMapData);
        String calculatedHmac = encryptUseCase.hmacSHA256(originalMessage, keyString);

        boolean isVerifiedHmac = clientDataContainer.verifyHmacSHA256(calculatedHmac);
        boolean isVerifiedMsgType = clientDataContainer.verifyReceivedMessageType("status");

        Map<String, String> responseMessage = new HashMap<>();
        responseMessage.put("resultCode", EnumResultCode.SUCCESS.getCode());
        responseMessage.put("resultMsg", EnumResultCode.SUCCESS.getValue());
        responseMessage.put("msgType", "SiteInfo");
        responseMessage.put("encryptData", encryptUseCase.encryptData(Objects.requireNonNull(encryptStringData), keyIv));
        responseMessage.put("verifyInfo", encryptUseCase.hmacSHA256(Objects.requireNonNull(encryptStringData), keyString));

        verifiedHmacAndType(responseMessage, isVerifiedHmac, isVerifiedMsgType);

        return ResponseEntity.ok(responseMessage);
    }

    @PostMapping("/regSiteInfo")
    public ResponseEntity<?> regSiteInfo(@RequestBody ClientDataContainer clientDataContainer) {
        String keyString = clientDataContainer.keyString();
        Map<String, String> keyIv = encryptUseCase.getKeyIv(keyString);
        byte[] plainBytes = encryptUseCase.decryptData(clientDataContainer, keyIv);
        String originalMessage = new String(plainBytes);
        ClientDataContainer decryptInfo = Utils.jsonStringToObject(originalMessage, ClientDataContainer.class);

        Map<String, String> responseMessage = new HashMap<>();
        ResponseEntity<?> validateResponse = validateRequiredValues(Objects.requireNonNull(decryptInfo), responseMessage);
        if (validateResponse != null) {
            return validateResponse;
        }

        String calculatedHmac = encryptUseCase.hmacSHA256(originalMessage, keyString);

        boolean isVerifiedHmac = clientDataContainer.verifyHmacSHA256(calculatedHmac);
        boolean isVerifiedMsgType = clientDataContainer.verifyReceivedMessageType("reg");

        verifiedHmacAndType(responseMessage, isVerifiedHmac, isVerifiedMsgType);

        responseMessage.put("resultCode", EnumResultCode.SUCCESS.getCode());
        responseMessage.put("resultMsg", EnumResultCode.SUCCESS.getValue());

        agencyUseCase.registerAgency(decryptInfo);

        String notificationData = Utils.mapToJSONString(clientDataContainer.notificationData());
        notiUseCase.sendNotification(profileSpecificAdminUrl + "/clientManagement/agency/register/email", notificationData);

        return ResponseEntity.ok(responseMessage);
    }

    @PostMapping("/cancelSiteInfo")
    public ResponseEntity<?> cancelSiteInfo(@RequestBody ClientDataContainer clientDataContainer) {
        String keyString = clientDataContainer.keyString();
        Map<String, String> keyIv = encryptUseCase.getKeyIv(keyString);
        byte[] plainBytes = encryptUseCase.decryptData(clientDataContainer, keyIv);
        String originalMessage = new String(plainBytes);

        String calculatedHmac = encryptUseCase.hmacSHA256(originalMessage, keyString);

        boolean isVerifiedHmac = clientDataContainer.verifyHmacSHA256(calculatedHmac);
        boolean isVerifiedMsgType = clientDataContainer.verifyReceivedMessageType("cancel");

        Map<String, String> responseMessage = new HashMap<>();
        responseMessage.put("resultCode", EnumResultCode.SUCCESS.getCode());
        responseMessage.put("resultMsg", EnumResultCode.SUCCESS.getValue());
        verifiedHmacAndType(responseMessage, isVerifiedHmac, isVerifiedMsgType);

        String notificationData = Utils.mapToJSONString(clientDataContainer.notificationData());
        notiUseCase.sendNotification(profileSpecificAdminUrl + "/clientManagement/agency/cancel", notificationData);

        return ResponseEntity.ok(responseMessage);
    }


    private void verifiedHmacAndType(Map<String, String> responseMessage, boolean isVerifiedHmac, boolean isVerifiedMsgType) {
        if (!isVerifiedHmac) {
            System.out.println("HMAC 검증에 실패하였습니다.");
            responseMessage.put("resultMsg", "HMAC 검증에 실패하였습니다.");
            responseMessage.put("resultCode", "9999");
            return;
        }
        if (!isVerifiedMsgType) {
            System.out.println("MsgType 검증이 실패하였습니다.");
            responseMessage.put("resultMsg", "MsgType 검증이 실패하였습니다.");
            responseMessage.put("resultCode", "9999");
        }
    }


    public ResponseEntity<?> validateRequiredValues(ClientDataContainer clientDataContainer, Map<String, String> responseMessage) {
        String field = clientDataContainer.checkRequiredFields(clientDataContainer);
        if (field != null && !field.isEmpty()) {
            responseMessage.put("resultMsg", field + " 필드가 비어 있습니다.");
            responseMessage.put("resultCode", "9999");
            return ResponseEntity.ok(responseMessage);
        }
        return null;
    }
}
