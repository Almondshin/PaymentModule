package com.modules.link.service.payment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.modules.link.enums.EnumResultCode;
import lombok.Getter;

public class PaymentDtos {

    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PaymentResponse {

        private final String resultCode;
        private final String resultMsg;
        private String msgType;
        private String encryptData;
        private String verifyInfo;

        public PaymentResponse(){
            this.resultCode = EnumResultCode.SUCCESS.getCode();
            this.resultMsg = EnumResultCode.SUCCESS.getMessage();
        }
        public PaymentResponse(String encryptData, String verifyInfo, String messageType) {
            this.resultCode = EnumResultCode.SUCCESS.getCode();
            this.resultMsg = EnumResultCode.SUCCESS.getMessage();
            this.msgType = messageType;
            this.encryptData = encryptData;
            this.verifyInfo = verifyInfo;
        }

    }
}
