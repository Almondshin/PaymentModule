package com.modules.adapter.in.web;

import com.modules.application.port.in.EncryptUseCase;
import com.modules.application.port.in.NotiUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> siteStatusNoti(@RequestBody Map<String, String> responseData) throws GeneralSecurityException {
        System.out.println("siteStatus 응답 : " + responseData.get("agencyId"));
        System.out.println("siteStatus 응답 : " + responseData.get("siteId"));
        System.out.println("siteStatus 응답 : " + responseData.get("siteStatus"));
        System.out.println("siteStatus 응답 : " + responseData.get("detail"));

        String plainData = encryptUseCase.mapToJSONString(responseData);

        String msgType = "NotifyStatusSite";
        String encryptData = encryptUseCase.encryptData(responseData.get("agencyId"), plainData);
        String verifyInfo = encryptUseCase.hmacSHA256(plainData, responseData.get("agencyId"));

        Map<String, String> requestStatusSiteMap = new HashMap<>();
        requestStatusSiteMap.put("agencyId", responseData.get("agencyId"));
        requestStatusSiteMap.put("msgType", msgType);
        requestStatusSiteMap.put("encryptData", encryptData);
        requestStatusSiteMap.put("verifyInfo", verifyInfo);

        String requestStatusSiteData = encryptUseCase.mapToJSONString(requestStatusSiteMap);

        String targetUrl = notiUseCase.getAgencyUrlByAgencyInfoKey(responseData.get("agencyId"), msgType);

        System.out.println("targetUrl : " + targetUrl);
        System.out.println("requestStatusSiteData : " + requestStatusSiteData);

        //targetUrl : 가맹점 NotiURL 입니다.
         notiUseCase.sendNotification(targetUrl, requestStatusSiteData);

        // 클라이언트에게 응답을 기대하지 않는 경우에도 "Success" 응답을 제공
        return ResponseEntity.ok("Success");
    }




}
