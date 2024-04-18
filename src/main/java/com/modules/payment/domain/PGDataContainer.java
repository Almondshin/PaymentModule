package com.modules.payment.domain;


import com.modules.payment.application.config.Constant;
import com.modules.payment.application.utils.PGUtils;
import com.modules.pg.utils.EncryptUtil;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;

public class PGDataContainer {

    private static final String MCHT_CUST_NM = "상점이름";
    private static final String MCHT_CUST_ID = "상점아이디";
    private static final String INSTMT_MON = "00";
    private static final String CRC_CD = "KRW";
    private static final String CRC_ORD = "001";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATE_HYPHEN_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");


    private String version;
    private String method;
    private String bizType;
    private String encCd;

    private String mchtId;
    private String mchtTrdNo;
    private String trdDt;
    private String trdTm;

    private String pmtprdNm;
    private String mchtCustNm;
    private String mchtCustId;
    private String billKey;
    private String instmtMon;
    private String crcCd;
    private String trdAmt;

    private String orgTrdNo;
    private String cnclOrd;
    private String cnclAmt;

    private String pktHash;


    private String vldDtYear;
    private String vldDtMon;

    private String data;
    private String params;
    private String trdNo;

    private String outStatCd;
    private String outRsltCd;
    private String outRsltMsg;


    public String makeHashCipher(String licenseKey) {
        try {
            return EncryptUtil.digestSHA256(this.trdDt + this.trdTm + this.mchtId + this.mchtTrdNo + this.trdAmt + licenseKey);
        } catch (Exception e) {
            System.out.println("[" + this.mchtTrdNo + "][SHA256 HASHING] Hashing Fail! : " + e.toString());
            throw new RuntimeException(e);
        }
    }


    public PGDataContainer(String type, String mchtId, String tradeNum, String cnclAmt) {
        String trdDt = LocalDateTime.now().format(DATE_FORMATTER);
        String trdTm = LocalDateTime.now().format(TIME_FORMATTER);
        if (type.equals("cancel_params")) {
            this.mchtId = mchtId;
            this.version = "0A19";
            this.method = "CA";
            this.bizType = "C0";
            this.encCd = "23";
            this.mchtTrdNo = tradeNum;
            this.trdDt = trdDt;
            this.trdTm = trdTm;
        }
        if (type.equals("cancel_data")) {
            makeCancelPGData(trdDt, trdTm, mchtId, cnclAmt);
        }
    }


    public PGDataContainer(String type, String mchtId, String tradeNum, String billKey, String trdAmt, String productName) {
        this.mchtId = mchtId;
        this.mchtTrdNo = tradeNum;
        String trdDt = LocalDateTime.now().format(DATE_FORMATTER);
        String trdTm = LocalDateTime.now().format(TIME_FORMATTER);
        if (type.equals("bill_params")) {
            this.mchtId = mchtId;
            this.version = "0A19";
            this.method = "CA";
            this.bizType = "B0";
            this.encCd = "23";
            this.mchtTrdNo = tradeNum;
            this.trdDt = trdDt;
            this.trdTm = trdTm;
        }
        if (type.equals("bill_data")) {
            makeBillPGData("상품명", trdDt, trdTm, billKey, mchtId, trdAmt);
        }
    }

    private void makeCancelPGData(String trdDt, String trdTm, String mchtId, String cnclAmt) {
        this.orgTrdNo = this.mchtTrdNo;
        this.crcCd = CRC_CD;
        this.cnclOrd = CRC_ORD;
        this.cnclAmt = cnclAmt;
        this.pktHash = pktHash(trdDt, trdTm, mchtId);
        this.cnclAmt = encodeAmount(cnclAmt);
    }

    private String pktHash(String trdDt, String trdTm, String mchtId) {
        Constant constant = new Constant();
        String hashPlain = String.join("", trdDt, trdTm, mchtId, this.trdAmt, constant.PAYMENT_LICENSE_KEY);
        try {
            return PGUtils.digestSHA256(hashPlain);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String encodeAmount(String amount) {
        Constant constant = new Constant();
        try {
            return Base64.getEncoder().encodeToString(PGUtils.aes256EncryptEcb(constant.PAYMENT_AES256_KEY, amount));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void makeBillPGData(String productName, String trdDt, String trdTm, String billKey, String mchtId, String trdAmt) {
        this.pmtprdNm = productName;
        this.mchtCustNm = MCHT_CUST_NM;
        this.mchtCustId = MCHT_CUST_ID;
        this.billKey = billKey;
        this.instmtMon = INSTMT_MON;
        this.crcCd = CRC_CD;
        this.pktHash = pktHash(trdDt, trdTm, mchtId);
        this.trdAmt = encodeAmount(trdAmt);
    }

    public Date tradeDate() {
         LocalDateTime trDate = LocalDateTime.parse(this.trdDt + this.trdTm, DATE_HYPHEN_FORMATTER);
        Instant instant = trDate.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    public String params() {
        if(this.params == null) {
            throw new NullPointerException("params is null");
        }
        return this.params;
    }

    public String data() {
        if(this.data == null) {
            throw new NullPointerException("data is null");
        }
        return this.data;
    }

    public String decryptedAmount() throws Exception {
        Constant constant = new Constant();
        byte[] decodeBase64 = PGUtils.decodeBase64(this.trdAmt);
        byte[] resultByte = PGUtils.aes256DecryptEcb(constant.PAYMENT_AES256_KEY, decodeBase64);
        return new String(resultByte, StandardCharsets.UTF_8);
    }


    public String billKeyExpireDate(){
        return this.vldDtYear + this.vldDtMon;
    }


    public String tradeNum(){
        return this.mchtTrdNo;
    }

    public String pgTradeNum(){
        return this.trdNo;
    }

    public String paymentType(){
        return this.method;
    }

    public String billKey(){
        return this.billKey;
    }

    public String outStatusCode(){
        return this.outStatCd;
    }

    public String failData(){
        return "{\"outStatCd\":" + this.outStatCd + ","
                + "\"outRsltCd\":" + this.outRsltCd + ","
                + "\"outRsltMsg\":" + this.outRsltMsg + "}";

    }

}