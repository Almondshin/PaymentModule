package com.modules.link.controller.exception.handler;

import com.modules.link.application.service.exception.InvalidStatusException;
import com.modules.link.controller.container.PaymentResponse;
import com.modules.link.application.service.exception.NotFoundProductsException;
import com.modules.link.application.service.exception.InvalidStartDateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PaymentExceptionHandler {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(InvalidStartDateException.class)
    public ResponseEntity<PaymentResponse<Void>> InvalidStartDateException(InvalidStartDateException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(PaymentResponse.error(ex.getEnumResultCode().getCode(), ex.getEnumResultCode().getMessage()));
    }

    @ExceptionHandler(NotFoundProductsException.class)
    public ResponseEntity<PaymentResponse<Void>> NotFoundProductsException(NotFoundProductsException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(PaymentResponse.error(ex.getEnumResultCode().getCode(), ex.getEnumResultCode().getMessage()));
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<PaymentResponse<Void>> InvalidStatusException(InvalidStatusException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(PaymentResponse.error(ex.getEnumResultCode().getCode(), ex.getEnumResultCode().getMessage()));
    }
}
