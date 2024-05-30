package com.modules.link.controller;

import com.modules.link.controller.container.PaymentReceived;
import com.modules.link.domain.agency.Agency;
import com.modules.link.domain.agency.AgencyId;
import com.modules.link.domain.agency.AgencyKey;
import com.modules.link.domain.agency.SiteId;
import com.modules.link.domain.payment.Products;
import com.modules.link.enums.EnumResultCode;
import com.modules.link.service.agency.AgencyDtos;
import com.modules.link.service.agency.AgencyService;
import com.modules.link.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = {"/agency/payment", "/payment"})
public class PaymentController {
    private final AgencyService agencyService;
    private final PaymentService paymentService;

    @PostMapping("/getPaymentInfo")
    public ApiResponse<AgencyDtos.AgencyResponse> getPaymentInfo(@RequestBody PaymentReceived receivedData) {
        receivedData.validData();
        AgencyId agencyId = AgencyId.of(receivedData.getAgencyId());
        AgencyKey agencyKey = agencyService.getAgencyKey(agencyId);
        Agency agency = agencyService.getAgency(SiteId.of(receivedData.getSiteId()));

        List<String> allProductRates = paymentService.findAll().stream().map(Products::getId).collect(Collectors.toList());
        List<String> commonProducts = agencyKey.getProductList(receivedData.getAgencyId())
                .stream()
                .filter(allProductRates::contains)
                .collect(Collectors.toList());




        return ApiResponse.success(new AgencyDtos.AgencyResponse(EnumResultCode.SUCCESS));
    }


}
