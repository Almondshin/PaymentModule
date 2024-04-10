package com.modules.payment.application.exceptions.exceptions;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ValueException extends RuntimeException{
    private final int offer;
    private final int clientOffer;
    private final int price;
    private final String clientPrice;
    private final String endDate;
    private final String clientEndDate;
    private final String agencyId;
    private final String siteId;

    public ValueException(int offer, int clientOffer, int price, String clientPrice, String endDate, String clientEndDate, String agencyId, String siteId) {
        this.offer = offer;
        this.clientOffer = clientOffer;
        this.price = price;
        this.clientPrice = clientPrice;
        this.endDate = endDate;
        this.clientEndDate = clientEndDate;
        this.agencyId = agencyId;
        this.siteId = siteId;
    }
}
