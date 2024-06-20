package com.modules.link.infrastructure.hectofinancial.service;

import com.modules.link.application.port.HectoFinancialServicePort;
import com.modules.link.infrastructure.hectofinancial.config.Constant;
import com.modules.link.infrastructure.hectofinancial.dto.HFDtos.NotiCADto;
import com.modules.link.infrastructure.hectofinancial.dto.HFDtos.NotiVADto;
import com.modules.link.infrastructure.hectofinancial.utils.HFUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class HectoFinancialAdapter {
    Logger logger = LoggerFactory.getLogger("HectoFinancialAdapter");

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TRADE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final String SUCCESS = "0021";
    private static final String PENDING_PAYMENT = "0051";

    private final Constant constant;
    private final HectoFinancialServicePort hectoFinancialServicePort;

    public HectoFinancialAdapter(Constant constant, HectoFinancialServicePort hectoFinancialServicePort) {
        this.constant = constant;
        this.hectoFinancialServicePort = hectoFinancialServicePort;
    }

    public String notiCAData(NotiCADto notiCADto) {
        Map<String, String> RES_PARAMS = new LinkedHashMap<>();
        resParams(RES_PARAMS, notiCADto);

        String hashPlain = createHashPlainText(notiCADto.getMchtTrdNo(), notiCADto.getTrdAmt(), notiCADto.getOutStatCd(), notiCADto.getMchtId(), notiCADto.getTrdDtm());
        String hashCipher = generateHashCipher(hashPlain, RES_PARAMS.get("mchtTrdNo"));

        return responseMessage(hashCipher, RES_PARAMS.get("mchtTrdNo"), RES_PARAMS.get("pktHash"), RES_PARAMS.get("outStatCd"), "", RES_PARAMS);
    }

    private void resParams(Map<String, String> resParams, NotiCADto notiCADto) {
        resParams.put("outStatCd", notiCADto.getOutStatCd());
        resParams.put("trdNo", notiCADto.getTrdNo());
        resParams.put("method", notiCADto.getMethod());
        resParams.put("bizType", notiCADto.getBizType());
        resParams.put("mchtId", notiCADto.getMchtId());
        resParams.put("mchtTrdNo", notiCADto.getMchtTrdNo());
        resParams.put("trdAmt", notiCADto.getTrdAmt());
        resParams.put("pktHash", notiCADto.getPktHash());
    }

    private String createHashPlainText(String mchtTrdNo, String trdAmt, String outStatCd, String mchtId, String trdDtm) {
        return mchtTrdNo + trdAmt + outStatCd + mchtId + trdDtm + constant.LICENSE_KEY;
    }

    private String generateHashCipher(String hashPlain, String mchtTrdNo) {
        String hashCipher = "";
        try {
            hashCipher = HFUtils.digestSHA256(hashPlain);
        } catch (Exception e) {
            logger.error("[" + mchtTrdNo + "][SHA256 HASHING] Hashing Fail! : " + e.toString());
        } finally {
            logger.info("[" + mchtTrdNo + "][SHA256 HASHING] Plain Text[" + hashPlain + "] ---> Cipher Text[" + hashCipher + "]");
        }
        return hashCipher;
    }

    public String notiVAData(NotiVADto notiVADto) {
        return null;
    }

    private boolean checkPktHash(String hashCipher, String pktHash) {
        return hashCipher.equals(pktHash);
    }

    private Map<String, String> parseParams(String[] pairs) {
        Map<String, String> parsedParams = new HashMap<>();
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                parsedParams.put(keyValue[0], keyValue[1]);
            }
        }
        return parsedParams;
    }

    private boolean isFirstRequest(String bizType) {
//        return isEmptyPayment() && (bizType.equals("B2") || bizType.equals("B0"));
        return true;
    }

    private String responseMessage(String hashCipher, String mchtTrdNo, String pktHash, String outStatCd, String mchtParam, Map<String, String> responseParam) {
        if (!checkPktHash(hashCipher, pktHash)) {
            logger.info("[" + mchtTrdNo + "][SHA256 Hash Check] hashCipher[" + hashCipher + "] pktHash[" + pktHash + "] equals?[FALSE]");
            throw new InvalidParameterException();
        }

        Map<String, String> parseParams = parseParams(mchtParam.split("&"));
        String agencyId = parseParams.get("agencyId");
        String siteId = parseParams.get("siteId");
        LocalDate parsedStartDate = LocalDate.parse(parseParams.get("startDate"), DATE_TIME_FORMATTER);
        LocalDate parsedEndDate = LocalDate.parse(parseParams.get("endDate"), DATE_TIME_FORMATTER);
        String rateSel = parseParams.get("rateSel");
        String offer = parseParams.get("offer");

        boolean resp = false;
        if (SUCCESS.equals(outStatCd)) {
            String method = responseParam.get("method");
            LocalDateTime parsedDate = LocalDateTime.parse(responseParam.get("trdDtm"), TRADE_DATE_FORMATTER);
            Date trDate = Date.from(parsedDate.atZone(ZoneId.systemDefault()).toInstant());
            Date startDate = Date.from(parsedStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(parsedEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            if ("CA".equals(method)) {
                resp = handleCA(responseParam, agencyId, siteId, rateSel, offer, trDate, startDate, endDate);
            } else if ("VA".equals(method)) {
                resp = handleVA(responseParam, agencyId, siteId, rateSel, offer, trDate, startDate, endDate);
            } else {
                throw new InvalidParameterException("Invalid method (CA, VA) : " + method);
            }
        } else if (PENDING_PAYMENT.equals(outStatCd)) {
            // Handle pending payment status if needed
        }

        if (resp) {
            logger.info("[" + mchtTrdNo + "][Result] OK");
            return "OK";
        } else {
            logger.info("[" + mchtTrdNo + "][Result] FAIL");
            return "FAIL";
        }
    }

    private boolean handleCA(Map<String, String> responseParam, String agencyId, String siteId, String rateSel, String offer, Date trDate, Date startDate, Date endDate) {
        if (isFirstRequest(responseParam.get("bizType"))) {
            String billKey = responseParam.get("billKey");
            hectoFinancialServicePort.processPaymentCA(responseParam, agencyId, siteId, rateSel, offer, trDate, startDate, endDate, billKey);
            return true;
        }
        return false;
    }

    private boolean handleVA(Map<String, String> responseParam, String agencyId, String siteId, String rateSel, String offer, Date trDate, Date startDate, Date endDate) {
        if (isFirstRequest(responseParam.get("bizType"))) {
            LocalDateTime parsedVBankExpireDate = LocalDateTime.parse(responseParam.get("expireDt"), TRADE_DATE_FORMATTER);
            Date vBankExpireDate = Date.from(parsedVBankExpireDate.atZone(ZoneId.systemDefault()).toInstant());
            hectoFinancialServicePort.processPaymentVA(responseParam, agencyId, siteId, rateSel, offer, trDate, startDate, endDate, vBankExpireDate);
            return true;
        }
        return false;
    }


}
