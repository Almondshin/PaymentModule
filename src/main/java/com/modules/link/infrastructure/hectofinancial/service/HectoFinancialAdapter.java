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
        resParamsCA(RES_PARAMS, notiCADto);

        String hashPlain = createHashPlainText(
                notiCADto.getOutStatCd(),
                notiCADto.getTrdDtm(),
                notiCADto.getMchtId(),
                notiCADto.getMchtTrdNo(),
                notiCADto.getTrdAmt()) + constant.LICENSE_KEY;

        String hashCipher = generateHashCipher(hashPlain, notiCADto.getMchtTrdNo());

        return responseMessage(hashCipher, RES_PARAMS);
    }

    private String createHashPlainText(String outStatCd, String trdDtm, String mchtId, String mchtTrdNo, String trdAmt) {
        return outStatCd + trdDtm + mchtId + mchtTrdNo + trdAmt;
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
        Map<String, String> RES_PARAMS = new LinkedHashMap<>();
        resParamsVA(RES_PARAMS, notiVADto);
        String hashPlain = createHashPlainText(
                notiVADto.getOutStatCd(),
                notiVADto.getTrdDtm(),
                notiVADto.getMchtId(),
                notiVADto.getMchtTrdNo(),
                notiVADto.getTrdAmt()) + constant.LICENSE_KEY;
        String hashCipher = generateHashCipher(hashPlain, notiVADto.getMchtTrdNo());

        return responseMessage(hashCipher, RES_PARAMS);
    }

    private void resParamsCA(Map<String, String> resParams, NotiCADto notiCADto) {
        resParams.put("outStatCd", notiCADto.getOutStatCd());
        resParams.put("trdNo", notiCADto.getTrdNo());
        resParams.put("method", notiCADto.getMethod());
        resParams.put("bizType", notiCADto.getBizType());
        resParams.put("mchtId", notiCADto.getMchtId());
        resParams.put("mchtTrdNo", notiCADto.getMchtTrdNo());
        resParams.put("trdAmt", notiCADto.getTrdAmt());
        resParams.put("trdDtm", notiCADto.getTrdDtm());
        resParams.put("mchtParam", notiCADto.getMchtParam());

        resParams.put("billKey", notiCADto.getBillKey());
        resParams.put("billKeyExpireDt", notiCADto.getBillKeyExpireDt());
        resParams.put("pktHash", notiCADto.getPktHash());
    }

    private void resParamsVA(Map<String, String> resParams, NotiVADto notiVADto) {
        resParams.put("bizType", notiVADto.getBizType());
        resParams.put("mchtTrdNo", notiVADto.getMchtTrdNo());
        resParams.put("method", notiVADto.getMethod());
        resParams.put("trdAmt", notiVADto.getTrdAmt());
        resParams.put("mchtParam", notiVADto.getMchtParam());
        resParams.put("vAcntNo", notiVADto.getVAcntNo());
        resParams.put("trdNo", notiVADto.getTrdNo());
        resParams.put("trdDtm", notiVADto.getTrdDtm());
        resParams.put("expireDt", notiVADto.getExpireDt());
        resParams.put("pktHash", notiVADto.getPktHash());
        resParams.put("outStatCd", notiVADto.getOutStatCd());
        resParams.put("mchtId", notiVADto.getMchtId());
        resParams.put("AcntPrintNm", notiVADto.getAcntPrintNm());
        resParams.put("bankNm", notiVADto.getBankNm());
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

    private String responseMessage(String hashCipher, Map<String, String> responseParam) {
        String mchtTrdNo = responseParam.get("mchtTrdNo");
        String pktHash = responseParam.get("pktHash");
        if (!hashCipher.equals(pktHash)) {
            logger.info("[" + mchtTrdNo + "][SHA256 Hash Check] hashCipher[" + hashCipher + "] pktHash[" + pktHash + "] equals?[FALSE]");
            throw new InvalidParameterException();
        }

        String outStatCd = responseParam.get("outStatCd");
        String mchtParam = responseParam.get("mchtParam");
        String method = responseParam.get("method");

        Map<String, String> parseParams = parseParams(mchtParam.split("&"));
        String agencyId = parseParams.get("agencyId");
        String siteId = parseParams.get("siteId");
        LocalDate parsedStartDate = LocalDate.parse(parseParams.get("startDate"), DATE_TIME_FORMATTER);
        LocalDate parsedEndDate = LocalDate.parse(parseParams.get("endDate"), DATE_TIME_FORMATTER);
        String rateSel = parseParams.get("rateSel");
        String offer = parseParams.get("offer");

        boolean resp = false;
        LocalDateTime parsedDate = LocalDateTime.parse(responseParam.get("trdDtm"), TRADE_DATE_FORMATTER);
        LocalDate trDate = LocalDate.from(parsedDate.atZone(ZoneId.systemDefault()).toInstant());
        LocalDate startDate = LocalDate.from(parsedStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        LocalDate endDate = LocalDate.from(parsedEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (SUCCESS.equals(outStatCd)) {
            switch (method) {
                case "CA":
                    resp = sendCAPayment(responseParam, agencyId, siteId, rateSel, offer, trDate, startDate, endDate);
                    // 제휴사 Noti
                    // 관리도구 Noti
                    break;
                case "VA":
                    resp = sendVAPayment(responseParam, agencyId, siteId, rateSel, offer, trDate, startDate, endDate);
                    // 제휴사 Noti
                    // 관리도구 Noti
                    break;
                default:
                    throw new InvalidParameterException("Invalid method (CA, VA) : " + method);
            }
        } else if (PENDING_PAYMENT.equals(outStatCd)) {
            resp = sendVAPendingPayment(responseParam, agencyId, siteId, rateSel, offer, trDate, startDate, endDate);
            // 제휴사 Noti
            // 관리도구 Noti
        }

        logger.info("[" + mchtTrdNo + "][Result] " + (resp ? "OK" : "FAIL"));
        return resp ? "OK" : "FAIL";
    }


    private boolean sendCAPayment(Map<String, String> responseParam, String agencyId, String siteId, String rateSel, String offer, LocalDate trDate, LocalDate startDate, LocalDate endDate) {
        if (hectoFinancialServicePort.isCurrentPayment(responseParam.get("trdNo")) && ("B2".equals(responseParam.get("bizType")) || "B0".equals(responseParam.get("bizType")))) {
            String billKey = responseParam.get("billKey");
            String billKeyExpireDt = responseParam.get("billKeyExpireDt");

            hectoFinancialServicePort.processPaymentCA(responseParam, agencyId, siteId, rateSel, offer, trDate, startDate, endDate, billKey, billKeyExpireDt);
            return true;
        }
        logger.info("already registered payment {}", responseParam.get("trdNo"));
        return false;
    }

    private boolean sendVAPayment(Map<String, String> responseParam, String agencyId, String siteId, String rateSel, String offer, LocalDate trDate, LocalDate startDate, LocalDate endDate) {
        if (!hectoFinancialServicePort.isCurrentPayment(responseParam.get("trdNo"))) {
            hectoFinancialServicePort.processPaymentVA(responseParam, agencyId, siteId, rateSel, offer, trDate, startDate, endDate);
            return true;
        }
        logger.info("already registered payment {}", responseParam.get("trdNo"));
        return false;
    }

    private boolean sendVAPendingPayment(Map<String, String> responseParam, String agencyId, String siteId, String rateSel, String offer, LocalDate trDate, LocalDate startDate, LocalDate endDate) {
        if (hectoFinancialServicePort.isCurrentPayment(responseParam.get("trdNo"))) {
            LocalDateTime parsedVBankExpireDate = LocalDateTime.parse(responseParam.get("expireDt"), TRADE_DATE_FORMATTER);
            LocalDate vBankExpireDate = LocalDate.from(parsedVBankExpireDate.atZone(ZoneId.systemDefault()).toInstant());
            hectoFinancialServicePort.processPaymentVAPending(responseParam, agencyId, siteId, rateSel, offer, trDate, startDate, endDate, vBankExpireDate);
            return true;
        }
        logger.info("already registered payment {}", responseParam.get("trdNo"));
        return false;
    }


}
