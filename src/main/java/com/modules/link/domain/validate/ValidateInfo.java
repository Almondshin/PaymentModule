package com.modules.link.domain.validate;

import com.modules.base.domain.ValueObject;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ValidateInfo extends ValueObject<ValidateInfo> {
    private String messageType;
    private String encryptDate;
    private String verifyInfo;

    @Builder
    public ValidateInfo(String messageType, String encryptDate, String verifyInfo) {
        this.messageType = messageType;
        this.encryptDate = encryptDate;
        this.verifyInfo = verifyInfo;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getEncryptDate() {
        return encryptDate;
    }

    public String getVerifyInfo() {
        return verifyInfo;
    }
}
