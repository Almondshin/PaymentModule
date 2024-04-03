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

public class BillKeyTest {

    Logger logger = LoggerFactory.getLogger("HFInitController");

    @Test
    public void billKeyTest(){
        Map<String, Object> requestData = new HashMap<>();
        Map<String, String> params = new HashMap<>();

        String ver = "0A19";
        String method = "CA";
        String bizType = "B0";
        String encCd = "23";
        String mchtId = "nxca_jt_gu";
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
        params.put("trdDt", trdDt); // 주문 날짜
        params.put("trdTm", trdTm);   // 주문 시간

        Map<String, String> data = new HashMap<>();
        String crcCd = "KRW";
        String biilKey = "SBILL_PGCAnxca_jt_gu20249964770206160258";

        data.put("pmtprdNm", "테스트상품");
        data.put("mchtCustNm", "상점이름");
        data.put("mchtCustId", "gyMerchantId");
        data.put("billKey", biilKey);
        data.put("instmtMon", "00");
        data.put("crcCd", crcCd);
        data.put("trdAmt", "1000");
        try {
            data.put("pktHash", EncryptUtil.digestSHA256(trdDt + trdTm + mchtId + mchtTrdNo + "1000" + "ST1009281328226982205"));
            data.put("trdAmt", Base64.getEncoder().encodeToString(EncryptUtil.aes256EncryptEcb("pgSettle30y739r82jtd709yOfZ2yK5K", data.get("trdAmt"))));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        requestData.put("params", params);
        requestData.put("data", data);
        String url = "https://tbgw.settlebank.co.kr/spay/APICardActionPay.do";

        HttpClientUtil httpClientUtil = new HttpClientUtil();
        String resData = httpClientUtil.sendApi(url, requestData, 5000, 25000);
        System.out.println(resData);
    }

}