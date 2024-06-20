package com.modules.link.application.service.validate;

import com.modules.link.application.service.exception.InvalidSiteIdInitialException;
import com.modules.link.domain.validate.ValidateInfo;
import com.modules.link.domain.validate.service.ValidateDomainService;
import com.modules.link.enums.EnumResultCode;
import com.modules.link.application.service.exception.HmacException;
import com.modules.link.application.service.exception.MessageTypeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateService {

    private final ValidateDomainService validateDomainService;

    public void validateHmacAndMsgType(ValidateInfo validateInfo, String keyString, String key, String iv) {
        String originalMessage = validateDomainService.originalMessage(validateInfo.getEncryptDate(), key, iv);
        if (!validateDomainService.verifyHmacSHA256(originalMessage, validateInfo.getVerifyInfo(), keyString)) {
            throw new HmacException(EnumResultCode.HmacError);
        }
        if (!validateDomainService.verifyMessageType(validateInfo.getMessageType(), keyString)) {
            throw new MessageTypeException(EnumResultCode.MsgTypeError);
        }
    }

    public String originalMessage(ValidateInfo validateInfo, String key, String iv) {
        return validateDomainService.originalMessage(validateInfo.getEncryptDate(), key, iv);
    }

    public String encryptData(String encryptData, String key, String iv) {
        return validateDomainService.encryptData(encryptData, key, iv);
    }

    public String hmacSHA256(String target, String keyString) {
        return validateDomainService.hmacSHA256(target, keyString);
    }

    public void isSiteIdStartWithInitial(String agencyId, String siteId) {
        if (!validateDomainService.isSiteIdStartWithInitial(agencyId, siteId)) {
            throw new InvalidSiteIdInitialException(EnumResultCode.IllegalArgument, agencyId, siteId);
        }
    }
}
