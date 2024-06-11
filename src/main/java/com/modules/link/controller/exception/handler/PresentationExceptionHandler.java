package com.modules.link.controller.exception.handler;

import com.modules.link.controller.exception.IllegalAgencyIdSiteIdException;
import com.modules.link.controller.exception.InvalidSiteIdInitialException;
import com.modules.link.controller.exception.NullAgencyIdSiteIdException;
import com.modules.link.enums.EnumAgency;
import com.modules.link.enums.EnumResultCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

@Slf4j
@RestControllerAdvice
public class PresentationExceptionHandler {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Getter
    public static class ResponseMessage {
        private final String resultCode;
        private final String resultMsg;

        public ResponseMessage(String resultCode, String resultMsg) {
            this.resultCode = EnumResultCode.fromCode(resultCode);
            this.resultMsg = resultMsg;
        }
    }

    @ExceptionHandler(IllegalAgencyIdSiteIdException.class)
    public ResponseEntity<?> IllegalAgencyIdSiteIdException(IllegalAgencyIdSiteIdException ex) {
        ResponseMessage responseMessage = new ResponseMessage(EnumResultCode.IllegalArgument.getCode(), EnumResultCode.IllegalArgument.getMessage());
        logger.info("S ------------------------------[Exception] - [IllegalAgencyIdSiteIdException] ------------------------------ S");
        logger.info("[Exception siteId] : [" + ex.getSiteId() + "]");
        logger.info("[Exception ResultCode] : [" + responseMessage.getResultCode() + "]");
        logger.info("[Exception ResultMsg] : [" + responseMessage.getResultMsg() + "]");
        logger.info("E ------------------------------[Exception] - [IllegalAgencyIdSiteIdException] ------------------------------ E");
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @ExceptionHandler(NullAgencyIdSiteIdException.class)
    public ResponseEntity<?> NullAgencyIdSiteIdException(NullAgencyIdSiteIdException ex) {
        ResponseMessage responseMessage = new ResponseMessage(EnumResultCode.NullPointArgument.getCode(), EnumResultCode.NullPointArgument.getMessage());
        logger.info("S ------------------------------[Exception] - [NullAgencyIdSiteIdException] ------------------------------ S");
        logger.info("[Exception siteId] : [" + ex.getSiteId() + "]");
        logger.info("[Exception ResultCode] : [" + responseMessage.getResultCode() + "]");
        logger.info("[Exception ResultMsg] : [" + responseMessage.getResultMsg() + "]");
        logger.info("E ------------------------------[Exception] - [NullAgencyIdSiteIdException] ------------------------------ E");
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @ExceptionHandler(InvalidSiteIdInitialException.class)
    public ResponseEntity<?> InvalidSiteIdInitialException(InvalidSiteIdInitialException ex) {
        String exceptionMessage = "'" + ex.getAgencyId() + "' 제휴사의 siteId는 '" + EnumAgency.initialCheck(ex.getAgencyId()) + "' 또는 '" + EnumAgency.initialCheck(ex.getAgencyId()).toLowerCase() + "'로 시작해야합니다. (" + ex.getSiteId() + ")";
        ResponseMessage responseMessage = new ResponseMessage(EnumResultCode.InvalidSiteIdInitial.getCode(), exceptionMessage);
        logger.info("S ------------------------------[Exception] - [InvalidSiteIdInitialException] ------------------------------ S");
        logger.info("[Exception siteId] : [" + ex.getSiteId() + "]");
        logger.info("[Exception ResultCode] : [" + responseMessage.getResultCode() + "]");
        logger.info("[Exception ResultMsg] : [" + responseMessage.getResultMsg() + "]");
        logger.info("E ------------------------------[Exception] - [InvalidSiteIdInitialException] ------------------------------ E");
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<?> EntityExistsException(EntityExistsException ex) {
        ResponseMessage responseMessage = new ResponseMessage(EnumResultCode.DuplicateMember.getCode(), EnumResultCode.DuplicateMember.getMessage());
        logger.info("S ------------------------------[Exception] - [EntityExistsException] ------------------------------ S");
        logger.info("[Exception ResultCode] : [" + responseMessage.getResultCode() + "]");
        logger.info("[Exception ResultMsg] : [" + responseMessage.getResultMsg() + "]");
        logger.info("E ------------------------------[Exception] - [EntityExistsException] ------------------------------ E");
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }


}
