package com.modules.link.controller;

import com.modules.link.controller.container.PaymentReceived;
import com.modules.link.service.payment.PaymentService;
import com.modules.link.service.payment.dto.PaymentDtos.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = {"/agency/payment", "/payment"})
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

//    @PostMapping("/getPaymentInfo")
//    public ResponseEntity<PaymentResponse> getPayment(@Valid @RequestBody PaymentReceived receivedData) {
//        Agency agency = paymentService.getAgency(SiteId.of(receivedData.getSiteId()));
//
//        return ResponseEntity.ok(new PaymentResponse());
//    }

}
