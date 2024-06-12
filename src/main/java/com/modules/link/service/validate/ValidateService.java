package com.modules.link.service.validate;

import com.modules.link.service.exception.HmacException;
import com.modules.link.service.exception.InvalidSiteIdInitialException;
import com.modules.link.service.exception.MessageTypeException;
import com.modules.link.domain.agency.AgencyId;
import com.modules.link.domain.agency.AgencyKey;
import com.modules.link.domain.agency.SiteId;
import com.modules.link.domain.validate.ValidateInfo;
import com.modules.link.domain.validate.service.ValidateDomainService;
import com.modules.link.enums.EnumResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateService {

    private final ValidateDomainService validateDomainService;

    public void validateHmacAndMsgType(ValidateInfo validateInfo, AgencyKey agencyKey) {
        String keyString = agencyKey.keyString();
        String originalMessage = validateDomainService.originalMessage(validateInfo.getEncryptDate(), agencyKey.getKey(), agencyKey.getIv());
        if (!validateDomainService.verifyHmacSHA256(originalMessage, validateInfo.getVerifyInfo(), keyString)) {
            throw new HmacException(EnumResultCode.HmacError);
        }
        if (!validateDomainService.verifyMessageType(validateInfo.getMessageType(), keyString)) {
            throw new MessageTypeException(EnumResultCode.MsgTypeError);
        }
    }

    public String originalMessage(ValidateInfo validateInfo, AgencyKey agencyKey) {
        return validateDomainService.originalMessage(validateInfo.getEncryptDate(), agencyKey.getKey(), agencyKey.getIv());
    }

    public String encryptData(String encryptData, AgencyKey agencyKey) {
        return validateDomainService.encryptData(encryptData, agencyKey.getKey(), agencyKey.getIv());
    }

    public String hmacSHA256(String target, AgencyKey agencyKey) {
        return validateDomainService.hmacSHA256(target, agencyKey.keyString());
    }

    public void isSiteIdStartWithInitial(AgencyId agencyId, SiteId siteId) {
        if (!validateDomainService.isSiteIdStartWithInitial(agencyId.toString(), siteId.toString())) {
            throw new InvalidSiteIdInitialException(EnumResultCode.IllegalArgument, agencyId.toString(), siteId.toString());
        }
    }
}
