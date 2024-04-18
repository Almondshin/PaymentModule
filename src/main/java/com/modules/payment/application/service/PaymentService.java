package com.modules.payment.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modules.payment.application.config.Constant;
import com.modules.payment.application.enums.*;
import com.modules.payment.application.exceptions.enums.EnumResultCode;
import com.modules.payment.application.exceptions.exceptions.UnregisteredAgencyException;
import com.modules.payment.application.port.in.PaymentUseCase;
import com.modules.payment.application.port.in.StatUseCase;
import com.modules.payment.application.port.out.load.LoadAgencyProductDataPort;
import com.modules.payment.application.port.out.load.LoadEncryptDataPort;
import com.modules.payment.application.port.out.load.LoadPaymentDataPort;
import com.modules.payment.application.port.out.load.LoadStatDataPort;
import com.modules.payment.application.port.out.save.SaveAgencyDataPort;
import com.modules.payment.application.port.out.save.SavePaymentDataPort;
import com.modules.payment.application.utils.HttpClientUtil;
import com.modules.payment.application.utils.PGUtils;
import com.modules.payment.application.utils.Utils;
import com.modules.payment.domain.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
public class PaymentService implements PaymentUseCase, StatUseCase {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private static final String SUCCESS = "0021";

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
        int randomNum = random.nextInt(1000000);
        String formattedRandomNum = String.format("%06d", randomNum);
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
                .orElseThrow(() -> new UnregisteredAgencyException(EnumResultCode.UnregisteredAgency));

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
        try {
            PGDataContainer pgDataContainer = Utils.jsonStringToObject(reqData, PGDataContainer.class);
            PGDataContainer params = Utils.jsonStringToObject(pgDataContainer.params(), PGDataContainer.class);
            PGDataContainer data = Utils.jsonStringToObject(pgDataContainer.data(), PGDataContainer.class);

            String targetUrl = makeTargetUrl(agency.agencyId(), "NotifyPaymentSiteInfo");

            String agencyId = agency.agencyId();
            String siteId = agency.siteId();

            if (getPaymentHistoryByTradeNum(params.pgTradeNum()).isEmpty()) {
                if (params.outStatusCode().equals(SUCCESS)) {
                    String decryptedAmount = data.decryptedAmount();
                    Date tradeDate = params.tradeDate();

                    Calendar cal = Calendar.getInstance();
                    Date regDate = cal.getTime();

                    Date startDate = autoPayStartDate();
                    Date endDate = autoPayEndDate();

                    String rateSel = products.rateSel();
                    String offer = products.baseOffer();

                    PaymentHistory paymentHistory = PaymentHistory.builder()
                            .tradeNum(params.tradeNum())
                            .pgTradeNum(params.pgTradeNum())
                            .agencyId(agencyId)
                            .siteId(siteId)
                            .paymentType(params.paymentType())
                            .rateSel(rateSel)
                            .amount(decryptedAmount)
                            .offer(offer)
                            .trTrace(EnumTradeTrace.USED.getCode())
                            .paymentStatus(EnumPaymentStatus.ACTIVE.getCode())
                            .trDate(tradeDate)
                            .startDate(startDate)
                            .endDate(endDate)
                            .billKey(data.billKey())
                            .billKeyExpireDate(data.billKeyExpireDate())
                            .regDate(regDate)
                            .extraAmountStatus(EnumExtraAmountStatus.PASS.getCode())
                            .build();

                    savePaymentDataPort.insertPayment(paymentHistory);
                    saveAgencyDataPort.updateAgency(agency, EnumPaymentStatus.ACTIVE.getCode());

                    updateExtraAmountStatus(agencyId, siteId, params.paymentType());

                    Map<String, String> jsonData = prepareJsonDataForNotification(agencyId, siteId, params.tradeNum());
                    Map<String, String> notifyPaymentData = prepareNotifyPaymentData(agencyId, siteId, startDate, endDate, rateSel, decryptedAmount);

                    //AdminNoti
                    System.out.println("어드민 결제 노티 완료 targetUrl : " + profileSpecificAdminUrl + ", Data : " + Utils.mapToJSONString(jsonData));
                    notiService.sendNotification(profileSpecificAdminUrl + "/clientManagement/agency/payment/noti", Utils.mapToJSONString(jsonData));

                    //가맹점Noti
                    System.out.println("가맹점 결제 노티 완료 targetUrl : " + targetUrl + ", Data : " + makeAgencyNotifyData(agencyId, notifyPaymentData));
                    notiService.sendNotification(targetUrl, makeAgencyNotifyData(agencyId, notifyPaymentData));
                } else {
                    Map<String, String> jsonData = prepareJsonDataForNotification(agencyId, siteId, params.tradeNum());
                    jsonData.put("fail", params.failData());
                    System.out.println("Admin 결제 실패 노티 완료 targetUrl : " + profileSpecificAdminUrl + ", Data : " + Utils.mapToJSONString(jsonData));
                    notiService.sendNotification(profileSpecificAdminUrl + "/clientManagement/agency/payment/noti", Utils.mapToJSONString(jsonData));

//                    System.out.println("가맹점 결제 실패 노티 완료 targetUrl : " + targetUrl + ", Data : " + makeAgencyNotifyData(agencyId, notifyPaymentData));
//                    notiService.sendNotification(targetUrl, makeAgencyNotifyData(agencyId, notifyPaymentData));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void processAgencyPayment(Agency info, List<PaymentHistory> paymentHistoryList) {
        int excessAmount = 0;
        if (paymentHistoryList.size() > 2) {
            Map<String, Integer> excessMap = getExcessAmount(getPaymentHistoryByAgency(info.checkedExtendable()));
            excessAmount = excessMap.get("excessAmount");
        }

        String productName = getAgencyProductByRateSel(info.selectRateSelBasedOnType("scheduled")).productName();
        String merchantId = constant.PAYMENT_PG_MID_AUTO;
        String tradeNum = makeTradeNum();

        PaymentHistory paymentHistory = paymentHistoryList.get(0);
        String billKey = paymentHistory.billKey();
        String amount = paymentHistory.paymentAmount() + excessAmount;

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("params", paymentHistory.pgDataContainer("bill_params", merchantId, tradeNum, "", "", ""));
        requestData.put("data", paymentHistory.pgDataContainer("bill_data", merchantId, tradeNum, billKey, amount, productName));

        String responseData = processPayment(requestData, constant.PAYMENT_BILL_SERVER_URL + "/spay/APICardActionPay.do");
        insertAutoPayPaymentHistory(info, getAgencyProductByRateSel(info.selectRateSelBasedOnType("basic")), responseData);
    }

    @Override
    public String processPayment(Map<String, Object> requestData, String url) {
        HttpClientUtil httpClientUtil = new HttpClientUtil();
        String response = httpClientUtil.sendApi(url, requestData, 5000, 25000);
        System.out.println(response);
        return response;
    }

    @Override
    public Map<String, Object> prepareCancelRequestData(PaymentHistory paymentHistory) {
        Map<String, Object> requestData = new HashMap<>();
        String merchantId = paymentHistory.isScheduledRateSel() ? constant.getPAYMENT_PG_CANCEL_MID_AUTO() : constant.getPAYMENT_PG_CANCEL_MID_CARD();
        String amount = paymentHistory.paymentAmount();
        String tradeNum = makeTradeNum();
        requestData.put("params", new PGDataContainer("cancel_params", merchantId, tradeNum, amount));
        requestData.put("data", new PGDataContainer("cancel_data", "", "", amount));
        return requestData;
    }

    @Override
    public List<StatDay> getUseCountBySiteId(String siteId, String startDate, String endDate) {
        return loadStatDataPort.findBySiteIdAndFromDate(siteId, startDate, endDate);
    }

    public String makeTargetUrl(String agencyId, String msgType) {
        Optional<AgencyInfoKey> agencyInfoKey = loadEncryptDataPort.getAgencyInfoKey(agencyId);
        if (agencyInfoKey.isPresent()) {
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

        AgencyInfoKey keyIv = encryptDataService.getKeyIv(agencyId);
        json.put("encryptData", encryptDataService.encryptData(encryptDataService.mapToJSONString(notifyPaymentData), keyIv));
        json.put("verifyInfo", encryptDataService.hmacSHA256(encryptDataService.mapToJSONString(notifyPaymentData), agencyId));

        return json.toString();
    }


    private Date autoPayStartDate() {
        LocalDateTime nextMonth = LocalDateTime.now().plusMonths(1).withDayOfMonth(1);
        return Date.from(nextMonth.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Date autoPayEndDate() {
        LocalDateTime lastDayOfMonth = LocalDateTime.now().plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        return Date.from(lastDayOfMonth.atZone(ZoneId.systemDefault()).toInstant());
    }

}
