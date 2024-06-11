package com.modules.link.service.validate;

import com.modules.link.domain.agency.AgencyKey;
import com.modules.link.domain.validate.ValidateInfo;
import com.modules.link.domain.validate.service.ValidateDomainService;
import com.modules.link.enums.EnumResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ValidateService {

    private final ValidateDomainService validateDomainService;

    public Optional<EnumResultCode> validateHmacAndMsgType(ValidateInfo validateInfo, AgencyKey agencyKey) {
        String keyString = agencyKey.keyString();
        String key = agencyKey.getKey();
        String iv = agencyKey.getIv();
        String originalMessage = validateDomainService.originalMessage(validateInfo.getEncryptDate(), key, iv);
        if (!validateDomainService.verifyHmacSHA256(originalMessage, validateInfo.getVerifyInfo(), keyString)) {
            return Optional.of(EnumResultCode.HmacError);
        }
        if (!validateDomainService.verifyMessageType(validateInfo.getMessageType(), keyString)) {
            return Optional.of(EnumResultCode.MsgTypeError);
        }
        return Optional.of(EnumResultCode.SUCCESS);
    }

    public String originalMessage(ValidateInfo validateInfo, AgencyKey agencyKey) {
        return validateDomainService.originalMessage(validateInfo.getEncryptDate(), agencyKey.getKey(), agencyKey.getIv());
    }

    public String encryptData(String encryptData, AgencyKey agencyKey) {
        return validateDomainService.encryptData(encryptData, agencyKey.getKey(), agencyKey.getIv());
    }

    public String hmacSHA256(String target, AgencyKey agencyKey) {
        return validateDomainService.hmacSHA256(target, agencyKey.getKey());
    }
}
