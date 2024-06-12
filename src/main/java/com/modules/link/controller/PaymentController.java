package com.modules.link.controller;

import com.modules.link.controller.container.PaymentReceived;
import com.modules.link.controller.container.PaymentResponse;
import com.modules.link.domain.agency.Agency;
import com.modules.link.domain.agency.AgencyId;
import com.modules.link.domain.agency.SiteId;
import com.modules.link.service.payment.PaymentService;
import com.modules.link.service.validate.ValidateService;
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
    private final ValidateService validateService;

    @PostMapping("/getPaymentInfo")
    public ResponseEntity<PaymentResponse> getPayment(@Valid @RequestBody PaymentReceived receivedData) {
        validateService.isSiteIdStartWithInitial(AgencyId.of(receivedData.getAgencyId()), SiteId.of(receivedData.getSiteId()));

        return ResponseEntity.ok(new PaymentResponse());
    }

}
