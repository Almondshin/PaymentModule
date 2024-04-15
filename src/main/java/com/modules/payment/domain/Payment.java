package com.modules.payment.domain;

import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class Payment {
    private String rateSel;
    private String searchedRateSel;
    private String extensionStatus;
    private String salesPrice;
    private String offer;


    private String refundAcntNo;
    private String vAcntNo;
    private String cnclAmt;
    private String trdAmt;
    private String vatAmt;
    private String taxFreeAmt;


    private static final String NUMBERS_ONLY_PATTERN = "^[0-9]+$";

    private boolean isValidSalesPrice(String salesPrice) {
        return salesPrice.matches(NUMBERS_ONLY_PATTERN);
    }

}
