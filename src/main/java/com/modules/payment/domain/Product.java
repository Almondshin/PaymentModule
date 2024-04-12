package com.modules.payment.domain;

import lombok.Builder;

import java.util.HashMap;

@Builder
public class Product {
    /* 결제 상품 */
    /*
    상품코드 (Product Code) : productCode
    상품이름 (Product Name): productName
    요금 (Price): price
    제공건수 (offer): offer
    서비스 제공 개월 (month): month
    건당요금 (Fee Per Case): feePerCase
    건당 초과 요금 (Excess Per Case): excessPerCase
    */
    private String productCode;
    private String productName;
    private String price;
    private String offer;
    private String month;
    private String feePerCase;
    private String excessPerCase;


    public String productName() {
        return this.productName;
    }

    public String excessPerCase(){
        return this.excessPerCase;
    }

    public HashMap<String, String> productMap() {
        HashMap<String, String> result = new HashMap<>();
        result.put("type", this.productCode);
        result.put("name", this.productName);
        result.put("price", this.price);
        result.put("basicOffer", this.offer);
        result.put("month", this.month);
        result.put("feePerCase", this.feePerCase);
        result.put("excessPerCase", this.excessPerCase);
        return result;
    }

    public int calculateOffer(int lastDate, int startDate) {
        int durations = lastDate - startDate + 1;
        int baseOffer = Integer.parseInt(this.offer) / Integer.parseInt(this.month);

        if (durations <= 14) {
            return (baseOffer) + (baseOffer * durations / lastDate);
        }
        return (baseOffer * durations / lastDate);
    }

    public double calculatePrice(int lastDate, int startDate) {
        int durations = lastDate - startDate + 1;
        int basePrice = Integer.parseInt(this.price) / Integer.parseInt(this.month);

        if (durations <= 14){
            return ((((double) (basePrice * durations) / lastDate) + basePrice) * 1.1);
        }
        return ((double) (basePrice * durations) / lastDate) + (basePrice * Integer.parseInt(this.month) - 1) * 1.1;
    }

    public String month() {
        return this.month;
    }


}
