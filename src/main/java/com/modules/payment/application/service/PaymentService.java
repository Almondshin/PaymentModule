package com.modules.payment.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modules.payment.application.config.Constant;
import com.modules.payment.application.enums.*;
import com.modules.payment.application.port.in.PaymentUseCase;
import com.modules.payment.application.port.in.StatUseCase;
import com.modules.payment.application.port.out.load.LoadAgencyProductDataPort;
import com.modules.payment.application.port.out.load.LoadEncryptDataPort;
import com.modules.payment.application.port.out.load.LoadPaymentDataPort;
import com.modules.payment.application.port.out.load.LoadStatDataPort;
import com.modules.payment.application.port.out.save.SaveAgencyDataPort;
import com.modules.payment.application.port.out.save.SavePaymentDataPort;
import com.modules.payment.application.utils.PGUtils;
import com.modules.payment.domain.*;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class PaymentService implements PaymentUseCase, StatUseCase {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    Logger logger = LoggerFactory.getLogger("PaymentService");

    @Value("${external.admin.url}")
    private String profileSpecificAdminUrl;
    private final Constant constant;
    private final EncryptDataService encryptDataService;
    private final NotiService notiService;
    private final LoadPaymentDataPort loadPaymentDataPort;
    private final SavePaymentDataPort savePaymentDataPort;
    private final SaveAgencyDataPort saveAgencyDataPort;
    private final LoadEncryptDataPort loadEncryptDataPort;
    private final LoadAgencyProductDataPort loadAgencyProductDataPort;
    private final LoadStatDataPort loadStatDataPort;


    public PaymentService(Constant constant, EncryptDataService encryptDataService, NotiService notiService, LoadPaymentDataPort loadPaymentDataPort, LoadEncryptDataPort loadEncryptDataPort, LoadAgencyProductDataPort loadAgencyProductDataPort, LoadStatDataPort loadStatDataPort, SavePaymentDataPort savePaymentDataPort, SaveAgencyDataPort saveAgencyDataPort) {
        this.constant = constant;
        this.encryptDataService = encryptDataService;
        this.notiService = notiService;
        this.loadPaymentDataPort = loadPaymentDataPort;
        this.loadEncryptDataPort = loadEncryptDataPort;
        this.loadAgencyProductDataPort = loadAgencyProductDataPort;
        this.loadStatDataPort = loadStatDataPort;
        this.savePaymentDataPort = savePaymentDataPort;
        this.saveAgencyDataPort = saveAgencyDataPort;
    }


    @Override
    public String makeTradeNum() {
        Random random = new Random();
        int randomNum = random.nextInt(10000);
        String formattedRandomNum = String.format("%04d", randomNum);
        return "GY" + formatter.format(LocalDateTime.now()) + formattedRandomNum;
    }

    @Override
    public List<PaymentHistory> getPaymentHistoryByAgency(Agency agency) {
        return loadPaymentDataPort.getPaymentHistoryByAgency(agency);
    }

    @Override
    public Map<String, Integer> getExcessAmount(List<PaymentHistory> list) {
        List<PaymentHistory> checkedList = list.stream()
                .filter(PaymentHistory::isPassed)
                .collect(Collectors.toList());

        if (checkedList.size() < 2) {
            return new HashMap<>();
        }

        PaymentHistory overPaymentTarget = checkedList.get(1);
        String agencyId = overPaymentTarget.agencyId();
        String billingBase = loadEncryptDataPort.getAgencyInfoKey(agencyId)
                .map(AgencyInfoKey::billingBase)
                .orElse(null);

        if (agencyId == null) {
            return new HashMap<>();
        }
        if (billingBase == null) {
            return new HashMap<>();
        }

        List<StatDay> findStatDayList = getUseCountBySiteId(
                overPaymentTarget.chainSiteId(),
                overPaymentTarget.convertStartDate(),
                overPaymentTarget.convertEndDate()
        );

        long useCountSum = getUseCount(billingBase, findStatDayList);
        Product products = getAgencyProductByRateSel(overPaymentTarget.rateSel());
        int calcExcessCount = overPaymentTarget.calculateExcessCount(useCountSum);

        saveAgencyDataPort.updateAgencyExcessCount(new Agency("excess", agencyId, overPaymentTarget.chainSiteId()), calcExcessCount);
        savePaymentDataPort.updatePaymentUseCount(overPaymentTarget, useCountSum);

        Map<String, Integer> result = new HashMap<>();
        result.put("excessCount", calcExcessCount);
        result.put("excessAmount", calcExcessCount > 0 ? (int) (calcExcessCount * Integer.parseInt(products.excessPerCase()) * 1.1) : 0);

        return result;
    }


    @Override
    public Optional<PaymentHistory> getPaymentHistoryByTradeNum(String pgTradeNum) {
        return loadPaymentDataPort.getPaymentHistoryByTradeNum(pgTradeNum);
    }


    @Override
    public Product getAgencyProductByRateSel(String rateSel) {
        Optional<Product> productOptional = loadAgencyProductDataPort.getAgencyProductByRateSel(rateSel);
        if (productOptional.isPresent()) {
            return productOptional.get();
        }
        throw new NoSuchElementException("Product with rateSel: " + rateSel + " not found");
    }

    @Override
    public void insertAutoPayPaymentHistory(Agency agency, Product products, String reqData) {
        System.out.println(reqData);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ObjectMapper mapper = new ObjectMapper();

        try {
            Map<String, Object> map = mapper.readValue(reqData, new TypeReference<>() {
            });
            String params = mapper.writeValueAsString(map.get("params"));
            String data = mapper.writeValueAsString(map.get("data"));
            Map<String, String> paramsMap = mapper.readValue(params, new TypeReference<>() {
            });
            Map<String, String> dataMap = mapper.readValue(data, new TypeReference<>() {
            });

            System.out.println("outStatCd : " + paramsMap.get("outStatCd"));
            System.out.println("outRsltCd : " + paramsMap.get("outRsltCd"));
            System.out.println("outRsltMsg : " + paramsMap.get("outRsltMsg"));

            String targetUrl = makeTargetUrl(agency.agencyId(), "NotifyPaymentSiteInfo");


            String agencyId = agency.agencyId();
            String siteId = agency.siteId();

            if (getPaymentHistoryByTradeNum(paramsMap.get("trdNo")) == null) {
                if ("0021".equals(paramsMap.get("outStatCd"))) {
                    byte[] decodeBase64 = PGUtils.decodeBase64(dataMap.get("trdAmt"));
                    byte[] resultByte = PGUtils.aes256DecryptEcb(constant.PAYMENT_AES256_KEY, decodeBase64);
                    String decryptedAmount = new String(resultByte, "UTF-8");

                    DateTimeFormatter trdDtFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                    LocalDateTime trDate = LocalDateTime.parse(paramsMap.get("trdDt") + paramsMap.get("trdTm"), trdDtFormat);
                    Instant instant = trDate.atZone(ZoneId.systemDefault()).toInstant();
                    Date trDateAsDate = Date.from(instant);

                    Calendar startDateCal = Calendar.getInstance();
                    startDateCal.add(Calendar.MONTH, 1);
                    startDateCal.set(Calendar.DAY_OF_MONTH, startDateCal.getActualMinimum(Calendar.DAY_OF_MONTH));

                    Calendar endDateCal = Calendar.getInstance();
                    endDateCal.add(Calendar.MONTH, 1);
                    endDateCal.set(Calendar.DAY_OF_MONTH, endDateCal.getActualMaximum(Calendar.DAY_OF_MONTH));

                    Calendar cal = Calendar.getInstance();
                    Date regDate = cal.getTime();

                    Date startDate = sdf.parse(sdf.format(startDateCal.getTime()));
                    Date endDate = sdf.parse(sdf.format(endDateCal.getTime()));

                    HashMap<String, String> productMap = products.productMap();


                    PaymentHistory paymentHistory = PaymentHistory.builder()
                            .tradeNum(paramsMap.get("mchtTrdNo"))
                            .pgTradeNum(paramsMap.get("trdNo"))
                            .agencyId(agencyId)
                            .siteId(siteId)
                            .paymentType(paramsMap.get("method"))
                            .rateSel(productMap.get("type"))
                            .amount(decryptedAmount)
                            .offer(productMap.get("offer"))
                            .trTrace(EnumTradeTrace.USED.getCode())
                            .paymentStatus(EnumPaymentStatus.ACTIVE.getCode())
                            .trDate(trDateAsDate)
                            .startDate(startDate)
                            .endDate(endDate)
                            .billKey(dataMap.get("billKey"))
                            .billKeyExpireDate(dataMap.get("vldDtYear") + dataMap.get("vldDtMon"))
                            .regDate(regDate)
                            .extraAmountStatus(EnumExtraAmountStatus.PASS.getCode())
                            .build();

                    savePaymentDataPort.insertPayment(paymentHistory);
                    saveAgencyDataPort.updateAgency(agency, EnumPaymentStatus.ACTIVE.getCode());

                    updateExtraAmountStatus(agencyId, siteId, paramsMap.get("method"));

                    Map<String, String> jsonData = prepareJsonDataForNotification(agencyId, siteId, paramsMap.get("mchtTrdNo"));
                    Map<String, String> notifyPaymentData = prepareNotifyPaymentData(agencyId, siteId, startDate, endDate, productMap.get("type"), decryptedAmount);

                    //AdminNoti
                    System.out.println("어드민 결제 노티 완료 targetUrl : " + profileSpecificAdminUrl + ", Data : " + encryptDataService.mapToJSONString(jsonData));
                    notiService.sendNotification(profileSpecificAdminUrl + "/clientManagement/agency/payment/noti", encryptDataService.mapToJSONString(jsonData));

                    //가맹점Noti
                    System.out.println("가맹점 결제 노티 완료 targetUrl : " + targetUrl + ", Data : " + makeAgencyNotifyData(agencyId, notifyPaymentData));
                    notiService.sendNotification(targetUrl, makeAgencyNotifyData(agencyId, notifyPaymentData));
                } else {
                    Map<String, String> jsonData = prepareJsonDataForNotification(agencyId, siteId, paramsMap.get("mchtTrdNo"));
                    jsonData.put("fail", "{\"outStatCd\":" + paramsMap.get("outStatCd") + ","
                            + "\"outRsltCd\":" + paramsMap.get("outRsltCd") + ","
                            + "\"outRsltMsg\":" + paramsMap.get("outRsltMsg") + "}");
                    System.out.println("Admin 결제 실패 노티 완료 targetUrl : " + profileSpecificAdminUrl + ", Data : " + encryptDataService.mapToJSONString(jsonData));
                    notiService.sendNotification(profileSpecificAdminUrl + "/clientManagement/agency/payment/noti", encryptDataService.mapToJSONString(jsonData));

//                    System.out.println("가맹점 결제 실패 노티 완료 targetUrl : " + targetUrl + ", Data : " + makeAgencyNotifyData(agencyId, notifyPaymentData));
//                    notiService.sendNotification(targetUrl, makeAgencyNotifyData(agencyId, notifyPaymentData));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String makeTargetUrl(String agencyId, String msgType) {
        Optional<AgencyInfoKey> agencyInfoKey = loadEncryptDataPort.getAgencyInfoKey(agencyId);
        if (agencyInfoKey.isPresent()) {
            AgencyInfoKey info = agencyInfoKey.get();
            ObjectMapper mapper = new ObjectMapper();
            EnumAgency[] enumAgencies = EnumAgency.values();
            for (EnumAgency enumAgency : enumAgencies) {
                if (enumAgency.getCode().equals(agencyId)) {
                    switch (msgType) {
                        case "SiteStatus":
                            return enumAgency.getSiteStatusMsg();
                        case "RegAgencySiteInfo":
                            return enumAgency.getRegMsg();
                        case "CancelSiteInfo":
                            return enumAgency.getCancelMsg();
                        case "NotifyPaymentSiteInfo":
                            return enumAgency.getPaymentMsg();
                        case "NotifyStatusSite":
                            return enumAgency.getStatusMsg();
                        default:
                            return "Error: the given message type does not match known types";
                    }
                }
            }
            throw new IllegalArgumentException("The given agencyId does not match any known agencies");
        }
        return "";
    }

    private void updateExtraAmountStatus(String agencyId, String siteId, String paymentType) {
        List<PaymentHistory> paymentHistoryList = loadPaymentDataPort.getPaymentHistoryByAgency(new Agency("update", agencyId, siteId))
                .stream()
                .filter(PaymentHistory::isPassed)
                .collect(Collectors.toList());
        if (!paymentHistoryList.isEmpty() && paymentHistoryList.size() > 2) {
            savePaymentDataPort.updatePaymentExtraAmountStatus(paymentHistoryList.get(2));
        }
    }

    private long getUseCount(String billingBase, List<StatDay> statDays) {
        if (billingBase.equals(EnumBillingBase.INCOMPLETE.getCode())) {
            LongStream incompleteCounts = statDays.stream().mapToLong(StatDay::getIncompleteCnt);
            return incompleteCounts.sum();
        } else if (billingBase.equals(EnumBillingBase.SUCCESS_FINAL.getCode())) {
            LongStream successFinalCnt = statDays.stream().mapToLong(StatDay::getSuccessFinalCnt);
            return successFinalCnt.sum();
        } else {
            return 0L;
        }
    }

    @Override
    public List<StatDay> getUseCountBySiteId(String siteId, String startDate, String endDate) {
        return loadStatDataPort.findBySiteIdAndFromDate(siteId, startDate, endDate);
    }


    private Map<String, String> prepareJsonDataForNotification(String agencyId, String siteId, String trdNum) {
        Map<String, String> jsonData = new HashMap<>();
        jsonData.put("agencyId", agencyId);
        jsonData.put("siteId", siteId);
        jsonData.put("tradeNum", trdNum);
        return jsonData;
    }

    private Map<String, String> prepareNotifyPaymentData(String agencyId, String siteId, Date startDate, Date endDate, String rateSel, String salesPrice) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, String> notifyPaymentData = new HashMap<>();
        notifyPaymentData.put("agencyId", agencyId);
        notifyPaymentData.put("siteId", siteId);
        notifyPaymentData.put("startDate", sdf.format(startDate));
        notifyPaymentData.put("endDate", sdf.format(endDate));
        notifyPaymentData.put("rateSel", rateSel);
        notifyPaymentData.put("salesPrice", salesPrice);
        return notifyPaymentData;
    }


    private String makeAgencyNotifyData(String agencyId, Map<String, String> notifyPaymentData) {
        JSONObject json = new JSONObject();
        json.put("agencyId", agencyId);
        EnumAgency[] enumAgencies = EnumAgency.values();
        for (EnumAgency enumAgency : enumAgencies) {
            if (enumAgency.getCode().equals(agencyId)) {
                json.put("msgType", enumAgency.getPaymentMsg());
                break;
            }
        }

        //TODO
        // keyIv 받는 것 변경 진행 중 Map<String, String> -> 객체
        encryptDataService.getKeyIv(agencyId);
        Map<String, String> test = new HashMap<>();
        json.put("encryptData", encryptDataService.encryptData(encryptDataService.mapToJSONString(notifyPaymentData), test));
        json.put("verifyInfo", encryptDataService.hmacSHA256(encryptDataService.mapToJSONString(notifyPaymentData), agencyId));

        return json.toString();
    }
}
