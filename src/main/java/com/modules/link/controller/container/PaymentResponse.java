package com.modules.link.controller.container;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.modules.link.enums.EnumResultCode;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse<T> implements Serializable {
    private T body;
    private String resultCode;
    private String resultMsg;

    private PaymentResponse(T body) {
        this.body = body;
    }

    private PaymentResponse(String code, String message) {
        this.resultCode = code;
        this.resultMsg = message;
    }

    public String getMessage() {
        return resultMsg;
    }

    public String getCode() {
        return resultCode;
    }

    public T getBody() {
        return body;
    }

    public static <T> PaymentResponse<T> success(T body) {
        return new PaymentResponse<>(body);
    }

    public static PaymentResponse<Void> error(String code, String message) {
        return new PaymentResponse<>(code, message);
    }
}