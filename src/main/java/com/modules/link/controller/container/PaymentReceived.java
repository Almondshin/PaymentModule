package com.modules.link.controller.container;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PaymentReceived {
    private String agencyId;

    private String siteId;

    private String rateSel;
    private String startDate;

    private String endDate;
    private String salesPrice;
    private String offer;
    private String method;
}
