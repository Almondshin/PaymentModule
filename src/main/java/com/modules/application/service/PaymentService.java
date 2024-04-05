package com.modules.application.service;

import com.modules.adapter.in.models.ClientDataContainer;
import com.modules.adapter.in.models.PaymentDataContainer;
import com.modules.adapter.in.models.PaymentHistoryDataContainer;
import com.modules.adapter.out.payment.config.hectofinancial.Constant;
import com.modules.adapter.out.payment.utils.EncryptUtil;
import com.modules.application.domain.*;
import com.modules.application.domain.enums.*;
import com.modules.application.domain.model.*;
import com.modules.application.enums.*;
import com.modules.application.exceptions.enums.EnumResultCode;
import com.modules.application.exceptions.exceptions.NoExtensionException;
import com.modules.application.exceptions.exceptions.ValueException;
import com.modules.application.port.in.PaymentUseCase;
import com.modules.application.port.in.StatUseCase;
import com.modules.application.port.out.load.LoadAgencyProductDataPort;
import com.modules.application.port.out.load.LoadEncryptDataPort;
import com.modules.application.port.out.load.LoadPaymentDataPort;
import com.modules.application.port.out.load.LoadStatDataPort;
import com.modules.application.port.out.save.SaveAgencyDataPort;
import com.modules.application.port.out.save.SavePaymentDataPort;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modules.domain.*;
import com.modules.domain.enums.*;
import com.modules.domain.model.*;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.text.ParseException;
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
    private final AgencyService agencyService;
    private final EncryptDataService encryptDataService;
    private final NotiService notiService;
    private final LoadPaymentDataPort loadPaymentDataPort;
    private final SavePaymentDataPort savePaymentDataPort;
    private final SaveAgencyDataPort saveAgencyDataPort;
    private final LoadEncryptDataPort loadEncryptDataPort;
    private final LoadAgencyProductDataPort loadAgencyProductDataPort;
    private final LoadStatDataPort loadStatDataPort;


    public PaymentService(Constant constant, AgencyService agencyService, EncryptDataService encryptDataService, NotiService notiService, LoadPaymentDataPort loadPaymentDataPort, LoadEncryptDataPort loadEncryptDataPort, LoadAgencyProductDataPort loadAgencyProductDataPort, LoadStatDataPort loadStatDataPort, SavePaymentDataPort savePaymentDataPort, SaveAgencyDataPort saveAgencyDataPort) {
        this.constant = constant;
        this.agencyService = agencyService;
        this.encryptDataService = encryptDataService;
        this.notiService = notiService;
        this.loadPaymentDataPort = loadPaymentDataPort;
        this.loadEncryptDataPort = loadEncryptDataPort;
        this.loadAgencyProductDataPort = loadAgencyProductDataPort;
        this.loadStatDataPort = loadStatDataPort;
        this.savePaymentDataPort = savePaymentDataPort;
        this.saveAgencyDataPort = saveAgencyDataPort;
    }


    //TODO
    // [중요!] 최적화
    @Override
    public void checkMchtParams(ClientDataContainer clientDataContainer) throws ParseException {
        String clientPrice = clientDataContainer.getSalesPrice();
        Calendar startDateByCal = Calendar.getInstance();
        Calendar endDateByCal = Calendar.getInstance();
        Calendar yesterDayCal = Calendar.getInstance();
        yesterDayCal.add(Calendar.DATE, -1);
        Date yesterday = yesterDayCal.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String agencyId = clientDataContainer.getAgencyId();
        String siteId = clientDataContainer.getSiteId();
        String rateSel = clientDataContainer.getRateSel();
        if (sdf.parse(sdf.format(clientDataContainer.getStartDate())).before(yesterday)) {
            throw new NoExtensionException(EnumResultCode.NoExtension, siteId);
        }
        startDateByCal.setTime(sdf.parse(sdf.format(clientDataContainer.getStartDate())));
        int offer;
        double price;
        int clientOffer = Integer.parseInt(clientDataContainer.getOffer());
        String endDate = "";
        String clientEndDate = sdf.format(clientDataContainer.getEndDate());


        AgencyProducts agencyProducts = getAgencyProductByRateSel(rateSel);

        int lastDate = startDateByCal.getActualMaximum(Calendar.DATE);
        int startDate = startDateByCal.get(Calendar.DATE);

        int durations = lastDate - startDate + 1;
        int baseOffer = Integer.parseInt(agencyProducts.getOffer()) / Integer.parseInt(agencyProducts.getMonth());
        int basePrice = Integer.parseInt(agencyProducts.getPrice()) / Integer.parseInt(agencyProducts.getMonth());
        int dataMonth = Integer.parseInt(agencyProducts.getMonth());

        offer = (baseOffer * (dataMonth - 1)) + (baseOffer * durations / lastDate);
        price = ((((double) (basePrice * durations) / lastDate) + (basePrice * (dataMonth - 1))) * 1.1);

        endDateByCal.set(Calendar.MONTH, startDateByCal.get(Calendar.MONTH));
        System.out.println(startDateByCal.get(Calendar.MONTH));
        if (Integer.parseInt(agencyProducts.getMonth()) == 1) {
            if (durations <= 14) {
                endDateByCal.add(Calendar.MONTH, Integer.parseInt(agencyProducts.getMonth()));
                offer = (baseOffer) + (baseOffer * durations / lastDate);
                price = ((((double) (basePrice * durations) / lastDate) + basePrice) * 1.1);
            } else {
                offer = (baseOffer * durations / lastDate);
                price = (((double) (basePrice * durations) / lastDate) * 1.1);
            }
        } else {
            endDateByCal.add(Calendar.MONTH, Integer.parseInt(agencyProducts.getMonth()) - 1);
        }

        Optional<ClientDataContainer> info = agencyService.getAgencyInfo(new ClientDataContainer(agencyId, siteId));

        if (info.get().getExtensionStatus().equals(EnumExtensionStatus.EXTENDABLE.getCode())) {
            List<PaymentHistoryDataContainer> list = getPaymentHistoryByAgency(agencyId, siteId).stream()
                    .filter(e -> e.getTrTrace().equals(EnumTradeTrace.USED.getCode()))
                    .filter(e -> e.getExtraAmountStatus().equals(EnumExtraAmountStatus.PASS.getCode()))
                    .collect(Collectors.toList());

            if (sdf.parse(sdf.format(clientDataContainer.getStartDate())).before(info.get().getEndDate())) {
                throw new NoExtensionException(EnumResultCode.NoExtension, siteId);
            }
            endDateByCal.setTime(sdf.parse(sdf.format(clientDataContainer.getStartDate())));

            if (agencyProducts.getMonth().equals("1")) {
                if (durations <= 14) {
                    endDateByCal.add(Calendar.MONTH, 1);
                }
            } else {
                endDateByCal.add(Calendar.MONTH, Integer.parseInt(agencyProducts.getMonth()) - 1);
            }

            int excessCount;
            double excessAmount = 0;
            if (list.size() > 2) {
                excessCount = Integer.parseInt(list.get(1).getOffer()) - Integer.parseInt(list.get(1).getUseCount());
                if (excessCount < 0) {
                    excessAmount = Math.abs(excessCount) * Integer.parseInt(agencyProducts.getExcessPerCase()) * 1.1;
                }
            }
            price += excessAmount;
        }


        endDateByCal.set(Calendar.DAY_OF_MONTH, endDateByCal.getActualMaximum(Calendar.DAY_OF_MONTH));
        endDate = sdf.format(endDateByCal.getTime());
        if (offer != clientOffer || String.valueOf(Math.floor(price)).equals(clientPrice) || !endDate.equals(clientEndDate)) {
            throw new ValueException(offer, clientOffer, (int) Math.floor(price), clientPrice, endDate, clientEndDate, agencyId, siteId);
        }

        logger.info("S ------------------------------[AGENCY] - [setPaymentSiteInfo] ------------------------------ S");
        logger.info("[agencyId] : [" + agencyId + "]");
        logger.info("[siteId] : [" + siteId + "]");
        logger.info("[rateSel] : [" + agencyProducts.getRateSel() + ", " + agencyProducts.getName() + "]");
        logger.info("[startDate] : [" + sdf.format(startDateByCal.getTime()) + "]");
        logger.info("[endDate] : [" + endDate + "]");
        logger.info("[offer] : [" + offer + "]");
        logger.info("[price] : [" + (int) Math.floor(price) + "]");
    }

    @Override
    public String aes256EncryptEcb(ClientDataContainer clientDataContainer, String tradeNum, String trdDt, String trdTm) {
        String licenseKey = constant.LICENSE_KEY;
        String mchtId;
        if (clientDataContainer.getMethod().equals("card") && clientDataContainer.getRateSel().toLowerCase().contains("autopay")) {
            mchtId = constant.PG_MID_AUTO;
        } else if (clientDataContainer.getMethod().equals("card")) {
            mchtId = constant.PG_MID_CARD;
        } else {
            mchtId = constant.PG_MID;
        }
        String hashPlain = new PaymentDataContainer(
                mchtId,
                clientDataContainer.getMethod(),
                tradeNum,
                trdDt,
                trdTm,
                clientDataContainer.getSalesPrice()
        ).getHashPlain() + licenseKey;
        String hashCipher = "";
        /** SHA256 해쉬 처리 */
        try {
            hashCipher = EncryptUtil.digestSHA256(hashPlain);//해쉬 값
        } catch (Exception e) {
            logger.error("[" + tradeNum + "][SHA256 HASHING] Hashing Fail! : " + e.toString());
        } finally {
            logger.info("[" + tradeNum + "][SHA256 HASHING] Plain Text[" + hashPlain + "] ---> Cipher Text[" + hashCipher + "]");
        }
        return hashCipher;

    }

    @Override
    public HashMap<String, String> encodeBase64(ClientDataContainer clientDataContainer, String tradeNum) {
        String aesKey = constant.AES256_KEY;
//        HashMap<String, String> params = convertToMap(clientDataModel);
        HashMap<String, String> params = new HashMap<>();
        params.put("trdAmt", clientDataContainer.getSalesPrice());

        System.out.println("encode Base 64 " + clientDataContainer);
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                String aesPlain = params.get(key);
                if (!("".equals(aesPlain))) {
                    byte[] aesCipherRaw = EncryptUtil.aes256EncryptEcb(aesKey, aesPlain);
                    String aesCipher = EncryptUtil.encodeBase64(aesCipherRaw);

                    params.put(key, aesCipher);//암호화된 데이터로 세팅
                    logger.info("[" + tradeNum + "][AES256 Encrypt] " + key + "[" + aesPlain + "] ---> [" + aesCipher + "]");
                }
            }
        } catch (Exception e) {
            logger.error("[" + tradeNum + "][AES256 Encrypt] AES256 Fail! : " + e.toString());
        }
        return params;
    }

    @Override
    public List<PaymentHistoryDataContainer> getPaymentHistoryByAgency(String agencyId, String siteId) {
        List<PaymentHistory> paymentHistories = loadPaymentDataPort.getPaymentHistoryByAgency(new Agency(agencyId, siteId));
        return paymentHistories.stream()
                .map(this::convertClient)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentHistoryDataContainer getPaymentHistoryByTradeNum(String pgTradeNum) {
        Optional<PaymentHistory> optPaymentHistory = loadPaymentDataPort.getPaymentHistoryByTradeNum(pgTradeNum);
        if (optPaymentHistory.isPresent()) {
            PaymentHistory paymentHistory = optPaymentHistory.get();
            return convertClient(paymentHistory);
        }
        return null;
    }

    @Override
    public String makeTradeNum() {
        Random random = new Random();
        int randomNum = random.nextInt(10000);
        String formattedRandomNum = String.format("%04d", randomNum);
        return "GY" + formatter.format(LocalDateTime.now()) + formattedRandomNum;
    }

    @Override
    public AgencyProducts getAgencyProductByRateSel(String rateSel) {
        return loadAgencyProductDataPort.getAgencyProductByRateSel(rateSel);
    }

    @Override
    public Map<String, Integer> getExcessAmount(List<PaymentHistoryDataContainer> list) {
        SimpleDateFormat convertFormat = new SimpleDateFormat("yyyyMMdd");

        List<PaymentHistoryDataContainer> checkedList = list.stream()
                .filter(e -> e.getTrTrace().equals(EnumTradeTrace.USED.getCode()))
                .filter(e -> e.getExtraAmountStatus().equals(EnumExtraAmountStatus.PASS.getCode()))
                .collect(Collectors.toList());

        if (checkedList.size() < 2) {
            return new HashMap<>();
        }

        PaymentHistoryDataContainer overPaymentTarget = checkedList.get(1);
        String agencyId = overPaymentTarget.getAgencyId();
        String billingBase = loadEncryptDataPort.getAgencyInfoKey(agencyId)
                .map(AgencyInfoKey::getBillingBase)
                .orElse(null);

        if (agencyId == null) {
            return new HashMap<>();
        }
        if (billingBase == null) {
            return new HashMap<>();
        }

        Date startDate = overPaymentTarget.getStartDate();
        Date endDate = overPaymentTarget.getEndDate();

        String convertedStartDate = convertFormat.format(startDate);
        String convertedEndDate = convertFormat.format(endDate);

        List<StatDay> findStatDayList = getUseCountBySiteId(
                agencyId + "-" + overPaymentTarget.getSiteId(),
                convertedStartDate,
                convertedEndDate
        );

        long useCountSum = getUseCount(billingBase, findStatDayList);
        System.out.println("useCountSum : " + useCountSum + ", startDate : " + convertedStartDate + ", endDate : " + convertedEndDate + ", billingBase : " + billingBase);


        AgencyProducts products = getAgencyProductByRateSel(overPaymentTarget.getRateSel());
        int offer = Integer.parseInt(overPaymentTarget.getOffer());
        int excessCount = offer - (int) useCountSum;
        int calcExcessCount = excessCount < 0 ? Math.abs(excessCount) : 0;

        saveAgencyDataPort.updateAgencyExcessCount(new Agency(agencyId, overPaymentTarget.getSiteId()), calcExcessCount);
        savePaymentDataPort.updatePaymentUseCount(overPaymentTarget.getTradeNum(), overPaymentTarget.getPgTradeNum(), useCountSum);

        Map<String, Integer> result = new HashMap<>();
        result.put("excessCount", calcExcessCount);
        result.put("excessAmount", calcExcessCount > 0 ? (int) (calcExcessCount * Integer.parseInt(products.getExcessPerCase()) * 1.1) : 0);

        return result;
    }

    @Override
    public void insertAutoPayPaymentHistory(String agencyId, String siteId, AgencyProducts products, String reqData) {
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

            String targetUrl = makeTargetUrl(agencyId, "NotifyPaymentSiteInfo");


            if (getPaymentHistoryByTradeNum(paramsMap.get("trdNo")) == null) {
                if ("0021".equals(paramsMap.get("outStatCd"))) {
                    byte[] decodeBase64 = EncryptUtil.decodeBase64(dataMap.get("trdAmt"));
                    byte[] resultByte = EncryptUtil.aes256DecryptEcb(constant.AES256_KEY, decodeBase64);
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

                    PaymentHistory paymentHistory = PaymentHistory.builder()
                            .tradeNum(paramsMap.get("mchtTrdNo"))
                            .pgTradeNum(paramsMap.get("trdNo"))
                            .agencyId(agencyId)
                            .siteId(siteId)
                            .paymentType(paramsMap.get("method"))
                            .rateSel(products.getRateSel())
                            .amount(decryptedAmount)
                            .offer(products.getOffer())
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
                    saveAgencyDataPort.updateAgency(new Agency(agencyId, siteId), new Client(products.getRateSel(), startDate, endDate), EnumPaymentStatus.ACTIVE.getCode());

                    updateExtraAmountStatus(agencyId, siteId, paramsMap.get("method"));

                    Map<String, String> jsonData = prepareJsonDataForNotification(agencyId, siteId, paramsMap.get("mchtTrdNo"));
                    Map<String, String> notifyPaymentData = prepareNotifyPaymentData(agencyId, siteId, startDate, endDate, products.getRateSel(), decryptedAmount);

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
        List<PaymentHistory> paymentHistoryList = loadPaymentDataPort.getPaymentHistoryByAgency(new Agency(agencyId, siteId))
                .stream()
                .filter(e -> e.getExtraAmountStatus().equals(EnumExtraAmountStatus.PASS.getCode()))
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


    private String makeAgencyNotifyData(String agencyId, Map<String, String> notifyPaymentData) throws GeneralSecurityException {
        JSONObject json = new JSONObject();
        json.put("agencyId", agencyId);
        EnumAgency[] enumAgencies = EnumAgency.values();
        for (EnumAgency enumAgency : enumAgencies) {
            if (enumAgency.getCode().equals(agencyId)) {
                json.put("msgType", enumAgency.getPaymentMsg());
                break;
            }
        }
        json.put("encryptData", encryptDataService.encryptData(agencyId, encryptDataService.mapToJSONString(notifyPaymentData)));
        json.put("verifyInfo", encryptDataService.hmacSHA256(encryptDataService.mapToJSONString(notifyPaymentData), agencyId));

        return json.toString();
    }

    //TODO
    // Mapper 클래스로 뺄 필요가 있는지 확인 (ClientSideDataModel [DTO] <-> Domain)
    private PaymentHistoryDataContainer convertClient(PaymentHistory paymentHistory) {
        return new PaymentHistoryDataContainer(
                paymentHistory.getTradeNum(),
                paymentHistory.getPgTradeNum(),
                paymentHistory.getAgencyId(),
                paymentHistory.getSiteId(),
                paymentHistory.getPaymentType(),
                paymentHistory.getRateSel(),
                paymentHistory.getAmount(),
                paymentHistory.getOffer(),
                paymentHistory.getUseCount(),
                paymentHistory.getTrTrace(),
                paymentHistory.getPaymentStatus(),
                paymentHistory.getTrDate(),
                paymentHistory.getStartDate(),
                paymentHistory.getEndDate(),
                paymentHistory.getRcptName(),
                paymentHistory.getBillKey(),
                paymentHistory.getBillKeyExpireDate(),
                paymentHistory.getVbankName(),
                paymentHistory.getVbankCode(),
                paymentHistory.getVbankAccount(),
                paymentHistory.getVbankExpireDate(),
                paymentHistory.getRegDate(),
                paymentHistory.getModDate(),
                paymentHistory.getExtraAmountStatus(),
                paymentHistory.getMemo()
        );
    }
}
