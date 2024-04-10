package com.modules.payment.adapter.in.web;

import com.modules.payment.application.port.in.EncryptUseCase;
import com.modules.payment.application.port.in.NotiUseCase;
import com.modules.payment.application.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = {"/agency/noti", "/noti"})
public class NotiController {

    private final EncryptUseCase encryptUseCase;
    private final NotiUseCase notiUseCase;

    public NotiController(EncryptUseCase encryptUseCase, NotiUseCase notiUseCase) {
        this.encryptUseCase = encryptUseCase;
        this.notiUseCase = notiUseCase;
    }

    @PostMapping("/siteStatus")
    public ResponseEntity<String> siteStatusNoti(@RequestBody Map<String, String> responseData) {
        final String msgType = "NotifyStatusSite";
        final String agencyId = responseData.get("agencyId");

        final String plainData = encryptUseCase.mapToJSONString(responseData);
        final Map<String, String> keyIv = encryptUseCase.getKeyIv(agencyId);
        final String encryptData = encryptUseCase.encryptData(plainData, keyIv);
        final String verifyInfo = encryptUseCase.hmacSHA256(plainData, agencyId);

        final String targetUrl = notiUseCase.getAgencyUrlByAgencyInfoKey(agencyId, msgType);
        final String requestStatusSiteData = prepareRequestData(agencyId, msgType, encryptData, verifyInfo);

        System.out.println("targetUrl : " + targetUrl);
        System.out.println("requestStatusSiteData : " + requestStatusSiteData);

        notiUseCase.sendNotification(targetUrl, requestStatusSiteData);

        return ResponseEntity.ok("Success");
    }


    private String prepareRequestData(String agencyId, String msgType, String encryptData, String verifyInfo) {
        Map<String, String> requestStatusSiteMap = new HashMap<>();
        requestStatusSiteMap.put("agencyId", agencyId);
        requestStatusSiteMap.put("msgType", msgType);
        requestStatusSiteMap.put("encryptData", encryptData);
        requestStatusSiteMap.put("verifyInfo", verifyInfo);
        return Utils.mapToJSONString(requestStatusSiteMap);
    }


}
