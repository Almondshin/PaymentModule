package com.modules.adapter.out.payment.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HFResultDataModel {
    private String mchtId;             //상점아이디
    private String outStatCd;          //결과코드
    private String outRsltCd;          //거절코드
    private String outRsltMsg;         //결과메세지
    private String method;             //결제수단
    private String mchtTrdNo;          //상점주문번호
    private String mchtCustId;         //상점고객아이디
    private String trdNo;              //세틀뱅크 거래번호
    private String trdAmt;             //거래금액
    private String mchtParam;          //상점 예약필드
    private String authDt;             //승인일시
    private String authNo;             //승인번호
    private String reqIssueDt;        //채번요청일시
    private String intMon;             //할부개월수
    private String fnNm;               //카드사명
    private String fnCd;               //카드사코드
    private String pointTrdNo;         //포인트거래번호
    private String pointTrdAmt;        //포인트거래금액
    private String cardTrdAmt;         //신용카드결제금액
    private String vtlAcntNo;          //가상계좌번호
    private String expireDt;           //입금기한
    private String cphoneNo;           //휴대폰번호
    private String billKey;            //자동결제키
    private String csrcAmt;            //현금영수증 발급 금액(네이버페이)
}
