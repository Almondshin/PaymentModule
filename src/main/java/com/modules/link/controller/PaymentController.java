package com.modules.link.controller;

import com.modules.link.controller.container.PaymentReceived;
import com.modules.link.controller.container.PaymentResponse;
import com.modules.link.service.payment.PaymentService;
import com.modules.link.service.validate.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = {"/agency/payment", "/payment"})
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final ValidateService validateService;


    // getPaymentInfo 요청을 받으면,
    // 전달받은 파라미터들에 대한 검증이 필요하다.
    // 1. siteId는 제휴사의 이니셜로 시작하는가?
    // 2. 전달받은 startDate, rateSel이 비어있는가?
    // 위 조건이 다 만족한다면,
    // 응답 데이터를 조형하여 전달한다.
    // 응답데이터에는 companyName, bizName, name이 들어가야하고,
    // resultCode, resultMsg
    // listSel
    // profileUrl, profilePaymentUrl
    // excessAmount, excessCount
    // extenstionStatus
    // startDate
    @PostMapping("/getPaymentInfo")
    public ResponseEntity<PaymentResponse> getPayment(@Valid @RequestBody PaymentReceived receivedData) {
        validateService.isSiteIdStartWithInitial(receivedData.getAgencyId(), receivedData.getSiteId());
        String siteId = receivedData.getSiteId();
        paymentService.isSite(siteId);

        String rateSel = paymentService.decideRateSel(receivedData.getRateSel(), siteId);
        String startDate = paymentService.decideStartDate(receivedData.getStartDate(), siteId)
                .map(String::toUpperCase)
                .orElse("");
        paymentService.isScheduled(siteId);
        System.out.println("paymentService.excessCount(siteId) : " + paymentService.excessCount(siteId));
        System.out.println("paymentService.excessAmount(siteId) : " + paymentService.excessAmount(siteId));
        return ResponseEntity.ok(PaymentResponse.success("응답 데이터 조형"));
    }

    private static class getPaymentResponse {
        private String resultCode;
        private String resultMsg;

        private String rateSel;
        private String startDate;

        private List<Map<String, String>> listSel;

        private List<String> clientInfo;

        private String profileUrl;
        private String profilePaymentUrl;

        private String extensionStatus;
        private String excessCount;
        private String excessAmount;
    }


}
