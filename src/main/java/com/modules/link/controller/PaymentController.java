package com.modules.link.controller;

import com.modules.link.controller.container.PaymentReceived;
import com.modules.link.domain.agency.Agency;
import com.modules.link.domain.agency.SiteId;
import com.modules.link.service.agency.AgencyService;
import com.modules.link.service.payment.PaymentService;
import com.modules.link.service.payment.dto.PaymentDtos.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = {"/agency/payment", "/payment"})
@RequiredArgsConstructor
public class PaymentController {

    private final AgencyService agencyService;
    private final PaymentService paymentService;

    @PostMapping("/getPaymentInfo")
    public ResponseEntity<PaymentResponse> getPayment(@Valid @RequestBody PaymentReceived receivedData) {
        Agency agency = agencyService.getAgency(SiteId.of(receivedData.getSiteId()));

        return ResponseEntity.ok(new PaymentResponse());
    }


    // 예외 처리 핸들러 추가
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .filter(message -> message != null && !message.isEmpty())
                .findFirst()
                .orElse("Invalid input");
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
