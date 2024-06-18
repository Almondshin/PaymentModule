package com.modules.link.controller.exception.handler;

import com.modules.link.domain.exception.IllegalAgencyIdSiteIdException;
import com.modules.link.domain.exception.NoExtensionException;
import com.modules.link.service.exception.*;
import com.modules.link.domain.exception.NullAgencyIdSiteIdException;
import com.modules.link.enums.EnumAgency;
import com.modules.link.enums.EnumResultCode;
import com.modules.link.service.exception.NoSuchFieldException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityExistsException;

@Slf4j
@RestControllerAdvice
public class AgencyExceptionHandler {
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
        ResponseMessage responseMessage = new ResponseMessage(ex.getEnumResultCode().getCode(), ex.getEnumResultCode().getMessage());
        logger.info("S ------------------------------[Exception] - [IllegalAgencyIdSiteIdException] ------------------------------ S");
        logger.info("[Exception siteId] : [" + ex.getSiteId() + "]");
        logger.info("[Exception ResultCode] : [" + responseMessage.getResultCode() + "]");
        logger.info("[Exception ResultMsg] : [" + responseMessage.getResultMsg() + "]");
        logger.info("E ------------------------------[Exception] - [IllegalAgencyIdSiteIdException] ------------------------------ E");
//        log.error("Exception: IllegalAgencyIdSiteIdException, siteId: {}, resultCode: {}, resultMsg: {}", ex.getSiteId(), responseMessage.getResultCode(), responseMessage.getResultMsg());
//        return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @ExceptionHandler(NullAgencyIdSiteIdException.class)
    public ResponseEntity<?> NullAgencyIdSiteIdException(NullAgencyIdSiteIdException ex) {
        ResponseMessage responseMessage = new ResponseMessage(ex.getEnumResultCode().getCode(), ex.getEnumResultCode().getMessage());
        logger.info("S ------------------------------[Exception] - [NullAgencyIdSiteIdException] ------------------------------ S");
        logger.info("[Exception siteId] : [" + ex.getSiteId() + "]");
        logger.info("[Exception ResultCode] : [" + responseMessage.getResultCode() + "]");
        logger.info("[Exception ResultMsg] : [" + responseMessage.getResultMsg() + "]");
        logger.info("E ------------------------------[Exception] - [NullAgencyIdSiteIdException] ------------------------------ E");
//        log.error("Exception: NullAgencyIdSiteIdException, siteId: {}, resultCode: {}, resultMsg: {}", ex.getSiteId(), responseMessage.getResultCode(), responseMessage.getResultMsg());
//        return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @ExceptionHandler(InvalidSiteIdInitialException.class)
    public ResponseEntity<?> InvalidSiteIdInitialException(InvalidSiteIdInitialException ex) {
        String exceptionMessage = "'" + ex.getAgencyId() + "' 제휴사의 siteId는 '" + EnumAgency.initialCheck(ex.getAgencyId()) + "' 또는 '" + EnumAgency.initialCheck(ex.getAgencyId()).toLowerCase() + "'로 시작해야합니다. (" + ex.getSiteId() + ")";
        ResponseMessage responseMessage = new ResponseMessage(ex.getEnumResultCode().getCode(), exceptionMessage);
        logger.info("S ------------------------------[Exception] - [InvalidSiteIdInitialException] ------------------------------ S");
        logger.info("[Exception siteId] : [" + ex.getSiteId() + "]");
        logger.info("[Exception ResultCode] : [" + responseMessage.getResultCode() + "]");
        logger.info("[Exception ResultMsg] : [" + responseMessage.getResultMsg() + "]");
        logger.info("E ------------------------------[Exception] - [InvalidSiteIdInitialException] ------------------------------ E");
//        log.error("Exception: InvalidSiteIdInitialException, siteId: {}, resultCode: {}, resultMsg: {}", ex.getSiteId(), responseMessage.getResultCode(), responseMessage.getResultMsg());
//        return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<?> EntityExistsException(EntityExistsException ex) {
        ResponseMessage responseMessage = new ResponseMessage(EnumResultCode.DuplicateMember.getCode(), EnumResultCode.DuplicateMember.getMessage());
        logger.info("S ------------------------------[Exception] - [EntityExistsException] ------------------------------ S");
        logger.info("[Exception ResultCode] : [" + responseMessage.getResultCode() + "]");
        logger.info("[Exception ResultMsg] : [" + responseMessage.getResultMsg() + "]");
        logger.info("E ------------------------------[Exception] - [EntityExistsException] ------------------------------ E");
//        log.error("Exception: EntityExistsException, resultCode: {}, resultMsg: {}", responseMessage.getResultCode(), responseMessage.getResultMsg());
//        return new ResponseEntity<>(responseMessage, HttpStatus.CONFLICT);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @ExceptionHandler(HmacException.class)
    public ResponseEntity<?> HmacException(HmacException ex) {
        ResponseMessage responseMessage = new ResponseMessage(ex.getEnumResultCode().getCode(), ex.getEnumResultCode().getMessage());
        logger.info("S ------------------------------[Exception] - [HmacException] ------------------------------ S");
        logger.info("[Exception ResultCode] : [" + responseMessage.getResultCode() + "]");
        logger.info("[Exception ResultMsg] : [" + responseMessage.getResultMsg() + "]");
        logger.info("E ------------------------------[Exception] - [HmacException] ------------------------------ E");
//        log.error("Exception: HmacException, resultCode: {}, resultMsg: {}", responseMessage.getResultCode(), responseMessage.getResultMsg());
//        return new ResponseEntity<>(responseMessage, HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }


    @ExceptionHandler(MessageTypeException.class)
    public ResponseEntity<?> MessageTypeException(MessageTypeException ex) {
        ResponseMessage responseMessage = new ResponseMessage(ex.getEnumResultCode().getCode(), ex.getEnumResultCode().getMessage());
        logger.info("S ------------------------------[Exception] - [MessageTypeException] ------------------------------ S");
        logger.info("[Exception ResultCode] : [" + responseMessage.getResultCode() + "]");
        logger.info("[Exception ResultMsg] : [" + responseMessage.getResultMsg() + "]");
        logger.info("E ------------------------------[Exception] - [MessageTypeException] ------------------------------ E");
//        log.error("Exception: MessageTypeException, resultCode: {}, resultMsg: {}", responseMessage.getResultCode(), responseMessage.getResultMsg());
//        return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @ExceptionHandler(NoSuchFieldException.class)
    public ResponseEntity<?> NoSuchFieldException(NoSuchFieldException ex) {
        ResponseMessage responseMessage = new ResponseMessage(ex.getEnumResultCode().getCode(), ex.getFieldName() + ex.getEnumResultCode().getMessage());
        logger.info("S ------------------------------[Exception] - [NoSuchFieldException] ------------------------------ S");
        logger.info("[Exception ResultCode] : [" + responseMessage.getResultCode() + "]");
        logger.info("[Exception ResultMsg] : [" + responseMessage.getResultMsg() + "]");
        logger.info("E ------------------------------[Exception] - [NoSuchFieldException] ------------------------------ E");
//        log.error("Exception: MessageTypeException, resultCode: {}, resultMsg: {}", responseMessage.getResultCode(), responseMessage.getResultMsg());
//        return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> EntityNotFoundException(EntityNotFoundException ex) {
        ResponseMessage responseMessage = new ResponseMessage(ex.getEnumResultCode().getCode(), ex.getEnumResultCode().getMessage());
        logger.info("S ------------------------------[Exception] - [EntityNotFoundException] ------------------------------ S");
        logger.info("[Exception siteId] : [" + ex.getSiteId() + "]");
        logger.info("[Exception ResultCode] : [" + responseMessage.getResultCode() + "]");
        logger.info("[Exception ResultMsg] : [" + responseMessage.getResultMsg() + "]");
        logger.info("E ------------------------------[Exception] - [EntityNotFoundException] ------------------------------ E");
//        log.error("Exception: MessageTypeException, resultCode: {}, resultMsg: {}", responseMessage.getResultCode(), responseMessage.getResultMsg());
//        return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @ExceptionHandler(NoExtensionException.class)
    public ResponseEntity<?> NoExtensionException(NoExtensionException ex) {
        ResponseMessage responseMessage = new ResponseMessage(ex.getEnumResultCode().getCode(), ex.getEnumResultCode().getMessage());
        logger.info("S ------------------------------[Exception] - [NoExtensionException] ------------------------------ S");
        logger.info("[Exception ResultCode] : [" + responseMessage.getResultCode() + "]");
        logger.info("[Exception ResultMsg] : [" + responseMessage.getResultMsg() + "]");
        logger.info("E ------------------------------[Exception] - [NoExtensionException] ------------------------------ E");
//        log.error("Exception: MessageTypeException, resultCode: {}, resultMsg: {}", responseMessage.getResultCode(), responseMessage.getResultMsg());
//        return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }


}
