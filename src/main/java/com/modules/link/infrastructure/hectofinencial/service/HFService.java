package com.modules.link.infrastructure.hectofinencial.service;

import com.modules.link.infrastructure.hectofinencial.config.Constant;
import com.modules.link.infrastructure.hectofinencial.utils.HFUtils;
import com.modules.link.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class HFService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String AUTO_PAY = "autopay";
    private static final String CARD = "card";
    private static final String AUTO_PAY_CANCEL = "autopay_cancel";
    private static final String CARD_CANCEL = "card_cancel";
    private static final String VBANK = "vbank";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final Constant constant;

    public String getMerchantId(String type) {
        switch (type) {
            case AUTO_PAY:
                return constant.PG_MID_AUTO;
            case CARD:
                return constant.PG_MID_CARD;
            case VBANK:
                return constant.PG_MID;
            case AUTO_PAY_CANCEL:
                return constant.PG_CANCEL_MID_AUTO;
            case CARD_CANCEL:
                return constant.PG_CANCEL_MID_CARD;
            default:
                throw new IllegalStateException("Invalid type: " + type);
        }
    }

    public String makeTradeNum() {
        Random random = new Random();
        int randomNum = random.nextInt(1000000);
        String formattedRandomNum = String.format("%06d", randomNum);
        return "DS" + FORMATTER.format(LocalDateTime.now()) + formattedRandomNum;
    }

    public String getHashCipher(String method, String price, String productCode, String tradeNum, String trdDt, String trdTm) {
        String licenseKey = constant.LICENSE_KEY;
        String mchtId;
        if (method.equals(CARD) && productCode.toLowerCase().contains(AUTO_PAY)) {
            mchtId = constant.PG_MID_AUTO;
        } else if (method.equals(CARD)) {
            mchtId = constant.PG_MID_CARD;
        } else {
            mchtId = constant.PG_MID;
        }
        String hashPlain = mchtId + method + tradeNum + trdDt + trdTm + price + licenseKey;
        String hashCipher = "";
        /** SHA256 해쉬 처리 */
        try {
            hashCipher = HFUtils.digestSHA256(hashPlain);//해쉬 값
        } catch (Exception e) {
            logger.error("[" + tradeNum + "][SHA256 HASHING] Hashing Fail! : " + e.toString());
        } finally {
            logger.info("[" + tradeNum + "][SHA256 HASHING] Plain Text[" + hashPlain + "] ---> Cipher Text[" + hashCipher + "]");
        }
        return hashCipher;
    }

    public HashMap<String, String> getEncryptParams(String price, String tradeNum) {
        String aesKey = constant.AES256_KEY;
//        HashMap<String, String> params = convertToMap(clientDataModel);
        HashMap<String, String> params = new HashMap<>();
        params.put("tradeAmount", price);

        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();

                String aesPlain = params.get(key);
                if (!("".equals(aesPlain))) {
                    byte[] aesCipherRaw = HFUtils.aes256EncryptEcb(aesKey, aesPlain);
                    String aesCipher = HFUtils.encodeBase64(aesCipherRaw);

                    params.put(key, aesCipher);//암호화된 데이터로 세팅
                    logger.info("[" + tradeNum + "][AES256 Encrypt] " + key + "[" + aesPlain + "] ---> [" + aesCipher + "]");
                }
            }
        } catch (Exception e) {
            logger.error("[" + tradeNum + "][AES256 Encrypt] AES256 Fail! : " + e.toString());
        }
        return params;
    }

}
