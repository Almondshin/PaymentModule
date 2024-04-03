package com.modules.adapter.in.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modules.adapter.in.models.ClientDataContainer;
import com.modules.application.enums.EnumAgency;
import com.modules.application.exceptions.enums.EnumResultCode;
import com.modules.application.port.in.AgencyUseCase;
import com.modules.application.port.in.EncryptUseCase;
import com.modules.application.port.in.NotiUseCase;
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
        ObjectMapper objectMapper = new ObjectMapper();
        String keyString = clientDataContainer.keyString();
        Map<String, String> verifyMapData = clientDataContainer.makeVerifyMapData(clientDataContainer);
        Map<String, String> keyIv = encryptUseCase.getKeyIv(keyString);
        byte[] plainBytes = encryptUseCase.decryptData(clientDataContainer, keyIv);
        String originalMessage = new String(plainBytes);
        ClientDataContainer decryptInfo = null;
        try {
            decryptInfo = objectMapper.readValue(originalMessage, ClientDataContainer.class);
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse JSON", e);
        }


        Optional<ClientDataContainer> info = agencyUseCase.getAgencyInfo(new ClientDataContainer(clientDataContainer, Objects.requireNonNull(decryptInfo)));
        Map<String, String> encryptMapData = new HashMap<>();

        if (info.isPresent()) {
            ClientDataContainer clientInfo = info.get();
            encryptMapData = clientInfo.makeEncryptMapData(clientInfo);
        }


        // KEY + Message => Hash 생성
        // Hash Data (VerifyInfo) => Server
        // Server : EncryptData -> Decrypt ((KEY,AES) + IV) -> DecryptData
        // -> DecryptData + KEY -> Hash 생성 (calculatedHmac)
        // compare VerifyInfo(Hash Data), CalculatedHmac(Hash Data)

//        encryptUseCase.hmacSHA256(,);

        boolean isVerifiedHmac = clientDataContainer.verifyHmacSHA256(keyString, clientDataContainer);
        boolean isVerifiedMsgType = verifyReceivedMessageType("status", verifyMapData);

        String encryptStringData = encryptUseCase.mapToJSONString(encryptMapData);

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
        ObjectMapper objectMapper = new ObjectMapper();
        String keyString = clientDataContainer.keyString();
        Map<String, String> verifyMapData = clientDataContainer.makeVerifyMapData(clientDataContainer);
        Map<String, String> keyIv = encryptUseCase.getKeyIv(keyString);
        byte[] plainBytes = encryptUseCase.decryptData(clientDataContainer, keyIv);
        String originalMessage = new String(plainBytes);
        ClientDataContainer decryptInfo = null;
        String registerMessage = null;

        Map<String, String> registerMap = clientDataContainer.makeRegisterMapData(clientDataContainer);

        try {
            decryptInfo = objectMapper.readValue(originalMessage, ClientDataContainer.class);
            registerMessage = objectMapper.writeValueAsString(registerMap);
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse JSON", e);
        }

        Map<String, String> responseMessage = new HashMap<>();
        ResponseEntity<?> validateResponse = validateRequiredValues(Objects.requireNonNull(decryptInfo), responseMessage);
        if (validateResponse != null) {
            return validateResponse;
        }

        boolean isVerifiedHmac = encryptUseCase.verifyHmacSHA256(keyString, originalMessage, verifyMapData);
        boolean isVerifiedMsgType = verifyReceivedMessageType("reg", verifyMapData);

        verifiedHmacAndType(responseMessage, isVerifiedHmac, isVerifiedMsgType);

        responseMessage.put("resultCode", EnumResultCode.SUCCESS.getCode());
        responseMessage.put("resultMsg", EnumResultCode.SUCCESS.getValue());

        agencyUseCase.registerAgency(decryptInfo);

        notiUseCase.sendNotification(profileSpecificAdminUrl + "/clientManagement/agency/register/email", registerMessage);

        return ResponseEntity.ok(responseMessage);
    }

    @PostMapping("/cancelSiteInfo")
    public ResponseEntity<?> cancelSiteInfo(@RequestBody ClientDataContainer clientDataContainer) {
        ObjectMapper objectMapper = new ObjectMapper();
        String keyString = clientDataContainer.keyString();
        Map<String, String> verifyMapData = clientDataContainer.makeVerifyMapData(clientDataContainer);
        Map<String, String> keyIv = encryptUseCase.getKeyIv(keyString);
        byte[] plainBytes = encryptUseCase.decryptData(clientDataContainer, keyIv);
        String originalMessage = new String(plainBytes);

        boolean isVerifiedHmac = encryptUseCase.verifyHmacSHA256(keyString, originalMessage, verifyMapData);
        boolean isVerifiedMsgType = verifyReceivedMessageType("cancel", verifyMapData);

        Map<String, String> responseMessage = new HashMap<>();
        responseMessage.put("resultCode", EnumResultCode.SUCCESS.getCode());
        responseMessage.put("resultMsg", EnumResultCode.SUCCESS.getValue());
        verifiedHmacAndType(responseMessage, isVerifiedHmac, isVerifiedMsgType);

        Map<String, String> cancelMap = clientDataContainer.makeCancelMapData(clientDataContainer);
        String cancelMessage = null;
        try {
            cancelMessage = objectMapper.writeValueAsString(cancelMap);
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse JSON", e);
        }
        notiUseCase.sendNotification(profileSpecificAdminUrl + "/clientManagement/agency/cancel", cancelMessage);

        return ResponseEntity.ok(responseMessage);
    }


    /**
     * 수신된 메시지 타입을 제휴사별 메세지 타입과 비교확인합니다.
     *
     * @param messageType   예상 메시지 타입
     * @param verifyMapData : receivedMsgType (전달 받은 메시지 유형), keyString (키값 (제휴사 id))
     * @return 받은 메시지 유형이 예상된 것과 일치하면 true, 그렇지 않으면 false
     */
    public boolean verifyReceivedMessageType(String messageType, Map<String, String> verifyMapData) {
        try {
            boolean isCancelType = messageType.equals("cancel");
            boolean isRegType = messageType.equals("reg");
            boolean isGetType = messageType.equals("status");

            String key = "";
            String value = "";

            for (Map.Entry<String, String> field : verifyMapData.entrySet()) {
                if (!Objects.equals(field.getKey(), "verifyInfo")) {
                    key = field.getKey();
                    value = field.getValue();
                    break;
                }
            }

            EnumAgency[] enumAgencies = EnumAgency.values();
            for (EnumAgency enumAgency : enumAgencies) {
                if (enumAgency.getCode().equals(key)) {
                    if (isCancelType) {
                        return enumAgency.getCancelMsg().equals(value);
                    } else if (isRegType) {
                        return enumAgency.getRegMsg().equals(value);
                    } else if (isGetType) {
                        return enumAgency.getStatusMsg().equals(value);
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
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
