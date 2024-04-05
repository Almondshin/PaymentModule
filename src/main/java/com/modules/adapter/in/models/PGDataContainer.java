package com.modules.adapter.in.models;

import com.modules.adapter.out.payment.utils.EncryptUtil;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Builder
public class PGDataContainer {

    private static final String MCHT_CUST_NM = "상점이름";
    private static final String MCHT_CUST_ID = "상점아이디";
    private static final String INSTMT_MON = "00";
    private static final String CRC_CD = "KRW";
    private static final String DATE_PATTERN = "yyyyMMdd";
    private static final String TIME_PATTERN = "HHmmss";


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

    public String makeHashCipher(String licenseKey) throws Exception {
        return EncryptUtil.digestSHA256(this.trdDt + this.trdTm + this.mchtId + this.mchtTrdNo + this.trdAmt + licenseKey);
    }

    public String encodeAmount(String aes256Key) throws Exception {
        return Base64.getEncoder().encodeToString(EncryptUtil.aes256EncryptEcb(aes256Key, this.trdAmt));
    }

    public PGDataContainer(String type, String mchtId, String tradeNum, String productName, String billKey, String hashCipher, String amount) {
        LocalDateTime now = LocalDateTime.now();
        this.mchtId = mchtId;
        this.mchtTrdNo = tradeNum;
        this.trdDt = now.toLocalDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN));
        this.trdTm = now.toLocalTime().format(DateTimeFormatter.ofPattern(TIME_PATTERN));

        if (type.equals("bill_params")) {
            makeBillPGParams();
        } else if (type.equals("bill_data")) {
            makeBillPGData(productName, billKey, hashCipher, amount);
        } else {
            throw new IllegalStateException("Invalid type: " + type);
        }
    }

    public PGDataContainer(String pgTradeNum, String cancelAmount, String hashCipher) {
        makeBillPGData(pgTradeNum, cancelAmount, hashCipher);
    }

    private void makeBillPGParams() {
        this.version = "0A19";
        this.method = "CA";
        this.bizType = "B0";
        this.encCd = "23";
    }

    private void makeBillPGData(String productName, String billKey, String hashCipher, String amount) {
        this.pmtprdNm = productName;
        this.mchtCustNm = MCHT_CUST_NM;
        this.mchtCustId = MCHT_CUST_ID;
        this.billKey = billKey;
        this.instmtMon = INSTMT_MON;
        this.crcCd = CRC_CD;
        this.pktHash = hashCipher;
        this.trdAmt = amount;
    }

    private void makeBillPGData(String pgTradeNum, String cancelAmount, String hashCipher) {
        this.orgTrdNo = pgTradeNum;
        this.crcCd = CRC_CD;
        this.cnclOrd = "001";
        this.cnclAmt = cancelAmount;
        this.pktHash = hashCipher;
    }


}