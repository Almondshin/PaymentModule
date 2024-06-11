package com.modules.link.controller;

import com.modules.link.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
