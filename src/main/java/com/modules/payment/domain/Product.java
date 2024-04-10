package com.modules.payment.domain;

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
}
