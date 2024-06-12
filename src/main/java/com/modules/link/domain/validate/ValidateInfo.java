package com.modules.link.domain.validate;

import com.modules.base.domain.ValueObject;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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
}
