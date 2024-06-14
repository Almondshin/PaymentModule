package com.modules.link.controller.container;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.modules.link.enums.EnumResultCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse<T> {
    private T body;
    private String message;

    private PaymentResponse(T body) {
        this.body = body;
    }

    private PaymentResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public T getBody() {
        return body;
    }

    public static <T> PaymentResponse<T> success(T body) {
        return new PaymentResponse<>(body);
    }

    public static PaymentResponse<Void> error(String message) {
        return new PaymentResponse<>(message);
    }

}