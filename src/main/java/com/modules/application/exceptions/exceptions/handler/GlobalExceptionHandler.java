package com.modules.application.exceptions.exceptions.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.modules.application.exceptions.enums.EnumResultCode;
import com.modules.application.exceptions.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(DuplicateMemberException.class)
    public ResponseEntity<?> DuplicateMemberException(DuplicateMemberException ex) {
        ResponseMessage responseMessage = new ResponseMessage(EnumResultCode.DuplicateMember.getCode(), EnumResultCode.DuplicateMember.getValue());
        logger.info("S ------------------------------[Exception] - [DuplicateMemberException] ------------------------------ S");
        logger.info("[Exception siteId] : [" + ex.getSiteId() + "]");
        logger.info("[Exception ResultCode] : [" + responseMessage.getResultCode() + "]");
        logger.info("[Exception ResultMsg] : [" + responseMessage.getResultMsg() + "]");
        logger.info("E ------------------------------[Exception] - [DuplicateMemberException] ------------------------------ E");
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
    @ExceptionHandler(IllegalAgencyIdSiteIdException.class)
    public ResponseEntity<?> IllegalAgencyIdSiteIdException(IllegalAgencyIdSiteIdException ex) {
        ResponseMessage responseMessage = new ResponseMessage(EnumResultCode.IllegalArgument.getCode(), EnumResultCode.IllegalArgument.getValue());
        logger.info("S ------------------------------[Exception] - [IllegalAgencyIdSiteIdException] ------------------------------ S");
        logger.info("[Exception siteId] : [" + ex.getSiteId() + "]");
        logger.info("[Exception ResultCode] : [" + responseMessage.getResultCode() + "]");
        logger.info("[Exception ResultMsg] : [" + responseMessage.getResultMsg() + "]");
        logger.info("E ------------------------------[Exception] - [IllegalAgencyIdSiteIdException] ------------------------------ E");
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }
    @ExceptionHandler(UnregisteredAgencyException.class)
    public ResponseEntity<?> UnregisteredAgencyException(UnregisteredAgencyException ex) {
        ResponseMessage responseMessage = new ResponseMessage(EnumResultCode.UnregisteredAgency.getCode(), EnumResultCode.UnregisteredAgency.getValue());
        logger.info("S ------------------------------[Exception] - [UnregisteredAgencyException] ------------------------------ S");
        logger.info("[Exception siteId] : [" + ex.getSiteId() + "]");
        logger.info("[Exception ResultCode] : [" + responseMessage.getResultCode() + "]");
        logger.info("[Exception ResultMsg] : [" + responseMessage.getResultMsg() + "]");
        logger.info("E ------------------------------[Exception] - [UnregisteredAgencyException] ------------------------------ E");
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @ExceptionHandler(NullAgencyIdSiteIdException.class)
    public ResponseEntity<?> NullAgencyIdSiteIdException(NullAgencyIdSiteIdException ex) {
        ResponseMessage responseMessage = new ResponseMessage(EnumResultCode.NullPointArgument.getCode(), EnumResultCode.NullPointArgument.getValue());
        logger.info("S ------------------------------[Exception] - [NullAgencyIdSiteIdException] ------------------------------ S");
        logger.info("[Exception siteId] : [" + ex.getSiteId() + "]");
        logger.info("[Exception ResultCode] : [" + responseMessage.getResultCode() + "]");
        logger.info("[Exception ResultMsg] : [" + responseMessage.getResultMsg() + "]");
        logger.info("E ------------------------------[Exception] - [NullAgencyIdSiteIdException] ------------------------------ E");
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @ExceptionHandler(NoExtensionException.class)
    public ResponseEntity<?> NoExtensionException(NoExtensionException ex) {
        ResponseMessage responseMessage = new ResponseMessage(EnumResultCode.NoExtension.getCode(), EnumResultCode.NoExtension.getValue());
        logger.info("S ------------------------------[Exception] - [NoExtensionException] ------------------------------ S");
        logger.info("[Exception siteId] : [" + ex.getSiteId() + "]");
        logger.info("[Exception ResultCode] : [" + responseMessage.getResultCode() + "]");
        logger.info("[Exception ResultMsg] : [" + responseMessage.getResultMsg() + "]");
        logger.info("E ------------------------------[Exception] - [NoExtensionException] ------------------------------ E");
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @ExceptionHandler(NotFoundProductsException.class)
    public ResponseEntity<?> NotFoundProductsException(NotFoundProductsException ex) {
        ResponseMessage responseMessage = new ResponseMessage(EnumResultCode.ReadyProducts.getCode(), EnumResultCode.ReadyProducts.getValue());
        logger.info("S ------------------------------[Exception] - [NotFoundProductsException] ------------------------------ S");
        logger.info("[Exception ResultCode] : [" + responseMessage.getResultCode() + "]");
        logger.info("[Exception ResultMsg] : [" + responseMessage.getResultMsg() + "]");
        logger.info("E ------------------------------[Exception] - [NotFoundProductsException] ------------------------------ E");
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> handleJsonProcessingException(JsonProcessingException ex) {
        // 상세한 오류 메시지를 사용자에게 응답하거나, 잘못된 요청 혹은 데이터 형식이 잘못되었다는 일반적인 메시지를 반환할 수 있습니다.
        return new ResponseEntity<>("Invalid data format", HttpStatus.BAD_REQUEST);
    }
}
