package com.modules.payment.application.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "payment")
public class Constant {
    public String PAYMENT_PG_MID;
    public String PAYMENT_PG_MID_CARD;
    public String PAYMENT_PG_MID_AUTO;
    public String PAYMENT_PG_CANCEL_MID_CARD;
    public String PAYMENT_PG_CANCEL_MID_AUTO;
    public String PAYMENT_LICENSE_KEY;
    public String PAYMENT_AES256_KEY;
    public String PAYMENT_BILL_SERVER_URL;
    public int PAYMENT_CONN_TIMEOUT;
    public int PAYMENT_READ_TIMEOUT;
}
