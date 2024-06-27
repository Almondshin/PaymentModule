package com.modules.link.controller;

import com.modules.link.controller.container.PaymentReceived;
import com.modules.link.controller.container.PaymentResponse;
import com.modules.link.enums.EnumExtensionStatus;
import com.modules.link.enums.EnumResultCode;
import com.modules.link.infrastructure.hectofinancial.service.HFService;
import com.modules.link.application.service.payment.PaymentService;
import com.modules.link.application.service.validate.ValidateService;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = {"/agency/payment", "/payment"})
@RequiredArgsConstructor
public class PaymentController {

    private static final String MERCHANT_NAME = "드림시큐리티";
    private static final String MERCHANT_ENGLISH_NAME = "dreamsecurity";
    private static final String AUTO_PAY = "autopay";
    private static final String CARD = "card";
    private static final String VBANK = "vbank";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");


    private static final String NOTIFY_DOMAIN = "/agency/payment/api/result/noti";
    private static final String NEXT_DOMAIN = "/agency/payment/api/result/next";
    private static final String CANCEL_DOMAIN = "/agency/payment/api/result/cancel";


    private final HFService hfService;
    private final PaymentService paymentService;
    private final ValidateService validateService;

    @Value("${external.url}")
    private String profileSpecificUrl;

    @Value("${external.payment.url}")
    private String profileSpecificPaymentUrl;

    @Value("${external.trade.url}")
    private String tradeUrl;

    @PostMapping("/getPayment")
    public ResponseEntity<PaymentResponse<?>> getPayment(@RequestBody PaymentReceived receivedData) {
        validateService.isSiteIdStartWithInitial(receivedData.getAgencyId(), receivedData.getSiteId());
        paymentService.isValidSite(receivedData.getSiteId());

        String agencyId = receivedData.getAgencyId();
        String siteId = receivedData.getSiteId();
        String rateSel = paymentService.decideRateSel(receivedData.getRateSel(), siteId);
        String startDate = paymentService.decideStartDate(receivedData.getStartDate(), siteId);
        paymentService.isScheduled(siteId);

        String extendable = "";
        int excessCount = 0, excessAmount = 0;
        if (paymentService.isExtendable(siteId)) {
            excessCount = paymentService.excessCount(siteId);
            excessAmount = paymentService.excessAmount(siteId);
            extendable = EnumExtensionStatus.EXTENDABLE.getCode();
        }
        return ResponseEntity.ok(PaymentResponse.success(
                GetPaymentResponse.builder()
                        .resultCode(EnumResultCode.SUCCESS.getCode())
                        .resultMsg(EnumResultCode.SUCCESS.getMessage())
                        .rateSel(rateSel)
                        .startDate(startDate)
                        .listSel(paymentService.listSel(agencyId))
                        .clientInfo(paymentService.clientInfo(siteId))
                        .profileUrl(profileSpecificUrl)
                        .profilePaymentUrl(profileSpecificPaymentUrl)
                        .extensionStatus(extendable)
                        .excessCount(excessCount)
                        .excessAmount(excessAmount)
                        .build()));
    }

    @ToString
    @Getter
    @Builder
    private static class GetPaymentResponse {
        String resultCode;
        String resultMsg;
        String rateSel;
        String startDate;
        List<Map<String, String>> listSel;
        List<String> clientInfo;
        String profileUrl;
        String profilePaymentUrl;
        String extensionStatus;
        int excessCount;
        int excessAmount;
    }


    @PostMapping("/setPayment")
    public ResponseEntity<PaymentResponse<?>> setPaymentInfo(@RequestBody PaymentReceived receivedData) {
        validateService.isSiteIdStartWithInitial(receivedData.getAgencyId(), receivedData.getSiteId());
        paymentService.isValidSite(receivedData.getSiteId());
        paymentService.decideRateSel(receivedData.getRateSel(), receivedData.getSiteId());
        paymentService.decideStartDate(receivedData.getStartDate(), receivedData.getSiteId());
        paymentService.verifyValue(
                receivedData.getAgencyId(),
                receivedData.getSiteId(),
                receivedData.getRateSel(),
                receivedData.getStartDate(),
                receivedData.getEndDate(),
                receivedData.getSalesPrice(),
                receivedData.getOffer());
        LocalDateTime now = LocalDateTime.now();

        String method = receivedData.getMethod();
        String rateSel = receivedData.getRateSel().toLowerCase();
        String merchantId = getMerchantId(method, rateSel);

        String tradeNum = hfService.makeTradeNum();
        String tradeDate = now.format(DATE_FORMATTER);
        String tradeTime = now.format(TIME_FORMATTER);
        String price = receivedData.getSalesPrice();

        return ResponseEntity.ok(PaymentResponse.success(
                SetPaymentResponse.builder()
                        .resultCode(EnumResultCode.SUCCESS.getCode())
                        .resultMsg(EnumResultCode.SUCCESS.getMessage())
                        .tradeServer(tradeUrl)
                        .merchantName(MERCHANT_NAME)
                        .merchantEnglishName(MERCHANT_ENGLISH_NAME)
                        .merchantId(merchantId)
                        .method(method)
                        .tradeDate(tradeDate)
                        .tradeTime(tradeTime)
                        .merchantTradeNum(tradeNum)
                        .tradeAmount(price)
                        .hashCipher(hfService.getHashCipher(method, price, rateSel, tradeNum, tradeDate, tradeTime))
                        .encryptParams(hfService.getEncryptParams(price, tradeNum))
                        .notifyUrl(profileSpecificUrl + NOTIFY_DOMAIN)
                        .nextUrl(profileSpecificUrl + NEXT_DOMAIN)
                        .cancelUrl(profileSpecificUrl + CANCEL_DOMAIN)
                        .build()));
    }

    private String getMerchantId(String method, String rateSel) {
        if (method.equals(CARD)) {
            return rateSel.contains(AUTO_PAY) ? hfService.getMerchantId(AUTO_PAY) : hfService.getMerchantId(CARD);
        } else {
            return hfService.getMerchantId(VBANK);
        }
    }

    @ToString
    @Getter
    @Builder
    private static class SetPaymentResponse {
        String resultCode;
        String resultMsg;
        String tradeServer;
        String merchantName;
        String merchantEnglishName;
        String merchantId;
        String method;
        String tradeDate;
        String tradeTime;
        String merchantTradeNum;
        String tradeAmount;
        String hashCipher;
        HashMap<String, String> encryptParams;
        String notifyUrl;
        String nextUrl;
        String cancelUrl;
    }


}
