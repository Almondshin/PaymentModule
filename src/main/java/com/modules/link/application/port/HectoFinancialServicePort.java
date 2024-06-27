package com.modules.link.application.port;

import com.modules.link.domain.agency.Agency;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

public interface HectoFinancialServicePort {
    void processPaymentCA(Map<String, String> responseParam, String agencyId, String siteId, String rateSel, String offer, LocalDate trDate, LocalDate startDate, LocalDate endDate, String billKey, String billKeyExpireDt);

    void processPaymentVA(Map<String, String> responseParam, String agencyId, String siteId, String rateSel, String offer, LocalDate trDate, LocalDate startDate, LocalDate endDate);

    void processPaymentVAPending(Map<String, String> responseParam, String agencyId, String siteId, String rateSel, String offer, LocalDate trDate, LocalDate startDate, LocalDate endDate, LocalDate vBankExpireDate);

    boolean isCurrentPayment(String pgTradeNum);

    class ResponseParamMapper {
        public static PaymentRequest mapToPaymentRequest(Map<String, String> responseParam) {
            return PaymentRequest.builder()
                    .tradeNum(responseParam.get("mchtTrdNo"))
                    .pgTradeNum(responseParam.get("trdNo"))
                    .method(responseParam.get("method"))
                    .amount(responseParam.get("trdAmt"))
                    .rcptName(responseParam.getOrDefault("AcntPrintNm",""))
                    .billKeyExpireDate(responseParam.getOrDefault("billKeyExpireDt",""))
                    .vBankName(responseParam.getOrDefault("bankNm",""))
                    .vBankAccount(responseParam.getOrDefault("vAcntNo",""))
                    .build();
        }
    }

    @ToString
    @Getter
    class PaymentRequest {
        private final String tradeNum;
        private final String pgTradeNum;
        private final String method;
        private final String amount;
        private final String rcptName;
        private final String billKeyExpireDate;
        private final String vBankName;
        private final String vBankAccount;

        @Builder
        public PaymentRequest(String tradeNum, String pgTradeNum, String method, String amount, String rcptName, String billKeyExpireDate, String vBankName, String vBankAccount) {
            this.tradeNum = tradeNum;
            this.pgTradeNum = pgTradeNum;
            this.method = method;
            this.amount = amount;
            this.rcptName = rcptName;
            this.billKeyExpireDate = billKeyExpireDate;
            this.vBankName = vBankName;
            this.vBankAccount = vBankAccount;
        }
    }
}
