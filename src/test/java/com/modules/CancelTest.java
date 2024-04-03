package com.modules;

import com.modules.adapter.out.payment.utils.EncryptUtil;
import com.modules.adapter.out.payment.utils.HttpClientUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CancelTest {

    Logger logger = LoggerFactory.getLogger("HFInitController");

    @Test
    public void cancelTest() {
        Map<String, Object> requestData = new HashMap<>();
        Map<String, String> params = new HashMap<>();

        String ver = "0A19"; // 전문의 버전 "0A19"고정값
        String method = "CA"; // 결제 수단 "CA" 고정값
        String bizType = "C0"; // 업무 구분코드 "C0" 고정값
        String encCd = "23"; //  암호화 구분 코드 "23" 고정값
        String mchtId = "nxca_jt_il"; // 상점아이디
        String mchtTrdNo = "GY" + UUID.randomUUID().toString().replace("-", "");
        LocalDateTime now = LocalDateTime.now();
        String trdDt = now.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String trdTm = now.toLocalTime().format(DateTimeFormatter.ofPattern("HHmmss"));

        params.put("mchtId", mchtId); // 헥토파이낸셜 부여 상점 아이디
        params.put("ver", ver); // 버전 (고정값)
        params.put("method", method); // 결제수단 (고정값)
        params.put("bizType", bizType); // 업무 구분 코드 (고정값)
        params.put("encCd", encCd);   // 암호화 구분 코드 (고정값)
        params.put("mchtTrdNo", mchtTrdNo); // 상점 주문번호
        params.put("trdDt", trdDt); // 취소 요청 일자
        params.put("trdTm", trdTm);   // 취소 요청 시간

        Map<String, String> data = new HashMap<>();
        String crcCd = "KRW";

        data.put("orgTrdNo", "STFP_PGCAnxca_jt_il0240308091731M1719328"); // 원거래번호 : 결제시 헥토에서 발급한 거래번호
        data.put("crcCd", crcCd); // 통화구분 "KRW" 고정값
        data.put("cnclOrd", "001"); // 취소 회차
        data.put("cnclAmt", "14903"); // 취소 금액 AES 암호화
        try {
            // 취소요청일자 + 취소요청시간 + 상점아이디 + 상점주문번호 + 취소금액(평문) + 해쉬키
            data.put("pktHash", EncryptUtil.digestSHA256(
                    trdDt + trdTm + mchtId + mchtTrdNo + "14903" + "ST1009281328226982205")
            );
            data.put("cnclAmt", Base64.getEncoder().encodeToString(EncryptUtil.aes256EncryptEcb("pgSettle30y739r82jtd709yOfZ2yK5K", data.get("cnclAmt"))));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        requestData.put("params", params);
        requestData.put("data", data);
        String url = "https://tbgw.settlebank.co.kr/spay/APICancel.do";

        HttpClientUtil httpClientUtil = new HttpClientUtil();
        String resData = httpClientUtil.sendApi(url, requestData, 5000, 25000);
        System.out.println(resData);
    }

}