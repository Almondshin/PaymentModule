package com.modules.payment.domain;


import lombok.Builder;

import java.util.Date;

@Builder
public class AgencyInfoKey {
    private String agencyId;
    private String agencyName;
    private String agencyProductType;
    private String agencyUrl;
    private String agencyKey;
    private String agencyIv;
    private Date regDate;
    private Date modDate;
    private String billingBase;


    public String billingBase() {
        return this.billingBase;
    }
    public String agencyProductType() {
        return this.agencyProductType;
    }
    public String agencyKey() {
        return this.agencyKey;
    }
    public String agencyIv() {
        return this.agencyIv;
    }
    public String agencyUrl() {
        return this.agencyUrl;
    }
}
