package com.modules.payment.adapter.in.web;

import com.modules.payment.application.enums.EnumExtensionStatus;
import com.modules.payment.application.exceptions.exceptions.IllegalStatusException;
import com.modules.payment.application.port.in.AgencyUseCase;
import com.modules.payment.application.port.in.PaymentUseCase;
import com.modules.payment.domain.*;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@Slf4j
@RestController
@RequestMapping(value = {"/agency/payment", "/payment"})
@RequiredArgsConstructor
public class PaymentController {

    private final AgencyUseCase agencyUseCase;
    private final PaymentUseCase paymentUseCase;

    @Value("${external.url}")
    private String profileSpecificUrl;

    @Value("${external.payment.url}")
    private String profileSpecificPaymentUrl;

    /**
     * 결제 정보 요청
     *
     * @param agency 필수 값 : agencyId, SiteId , 옵션 값 : RateSel, StartDate
     * @return resultCode, resultMsg, siteId, RateSel list,
     */
    @PostMapping("/getPaymentInfo")
    public ResponseEntity<?> getPaymentInfo(@RequestBody Agency agency) {
        try {
            int excessCount = 0;
            int excessAmount = 0;

            Agency agencyInfo = agencyUseCase.getAgencyInfo(agency).orElseThrow(() -> new IllegalArgumentException("Agency not found"));
            List<Map<String, String>> productTypes = agencyUseCase.getProductTypes(agency.agencyId());

            agencyInfo.isActive();
            agencyInfo.isScheduledRateSel();

            if (agencyInfo.isExtendable()) {
                Map<String, Integer> excessMap = paymentUseCase.getExcessAmount(
                        paymentUseCase.getPaymentHistoryByAgency(agencyInfo.checkedExtendable()));
                excessCount = excessMap.get("excessCount");
                excessAmount = excessMap.get("excessAmount");
            }
            return ResponseEntity.ok(paymentResponseData(agency, agencyInfo, productTypes, excessCount, excessAmount));
        } catch (IllegalStatusException e) {
            return ResponseEntity.ok(e.getEnumResultCode());
        }
    }

    /**
     * Sets payment site info.
     *
     * @param agency the client data model
     * @return the payment site info
     */
    @PostMapping("/setPaymentSiteInfo")
    public ResponseEntity<?> setPaymentSiteInfo(@RequestBody Agency agency) {
        List<PaymentHistory> list = paymentUseCase.getPaymentHistoryByAgency(agency).stream()
                .filter(PaymentHistory::isPassed)
                .collect(Collectors.toList());
        double excessAmount = 0;

        if (list.size() > 2) {
            Map<String, Integer> excessMap = paymentUseCase.getExcessAmount(
                    paymentUseCase.getPaymentHistoryByAgency(agency.checkedExtendable()));
            excessAmount = excessMap.get("excessAmount");
        }

        Product product = paymentUseCase.getAgencyProductByRateSel(agency.checkedRateSel(agency));
        int offer = product.calculateOffer(agency.intLastDate(), agency.intStartDate());
        double price = product.calculatePrice(agency.intLastDate(), agency.intStartDate());
        int month = Integer.parseInt(product.month());
        agency.checkedParams(excessAmount, offer, price, month);

        String tradeNum = paymentUseCase.makeTradeNum();
        PGResponseManager manager = agency.pgResponseMsg(tradeNum);
        return ResponseEntity.ok(manager);
    }



    private ResponseManager paymentResponseData(Agency agency, Agency searchedAgency, List<Map<String, String>> productTypes, int excessCount, int excessAmount) {
        List<String> clientInfo = searchedAgency.makeCompanyInfo();
        String rateSel = agency.checkedRateSel(searchedAgency);
        String startDate = agency.checkedStartDate(searchedAgency);

        if (searchedAgency.isExtendable()) {
            String extensionStatus = EnumExtensionStatus.EXTENDABLE.getCode();
            return new ResponseManager(clientInfo, rateSel, startDate, profileSpecificUrl, profileSpecificPaymentUrl, productTypes, extensionStatus, excessCount, excessAmount);
        }
        return new ResponseManager(clientInfo, rateSel, startDate, profileSpecificUrl, profileSpecificPaymentUrl, productTypes);
    }

}
