package com.modules.link.application.service.payment;

import com.modules.link.application.port.HectoFinancialServicePort;
import com.modules.link.application.service.exception.EntityNotFoundException;
import com.modules.link.application.service.exception.InvalidStatusException;
import com.modules.link.application.service.exception.NoExtensionException;
import com.modules.link.domain.agency.*;
import com.modules.link.domain.payment.*;
import com.modules.link.domain.payment.service.PaymentDomainService;
import com.modules.link.enums.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PaymentService implements HectoFinancialServicePort {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String SCHEDULED = "autopay";
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;
    private final StatDayRepository statDayRepository;
    private final AgencyKeyRepository agencyKeyRepository;
    private final AgencyRepository agencyRepository;
    private final SiteRepository siteRepository;


    private final PaymentDomainService paymentDomainService;

    @Transactional(readOnly = true)
    public void isValidSite(String siteId) {
        if (agencyRepository.find(SiteId.of(siteId)) == null || siteRepository.find(SiteId.of(siteId)) == null) {
            throw new EntityNotFoundException(EnumResultCode.UnregisteredAgency, siteId);
        }
        String siteStatus = agencyRepository.find(SiteId.of(siteId)).getAgencyStatus();
        //H
        if (siteStatus.equals(EnumSiteStatus.PENDING.getCode())) {
            throw new InvalidStatusException(EnumResultCode.PendingApprovalStatus, siteId);
        }
        //R
        if (siteStatus.equals(EnumSiteStatus.REJECT.getCode())) {
            throw new InvalidStatusException(EnumResultCode.RejectAgency, siteId);
        }
        //N
        if (siteStatus.equals(EnumSiteStatus.SUSPENDED.getCode())) {
            throw new InvalidStatusException(EnumResultCode.SuspendedSiteId, siteId);
        }

    }

    @Transactional(readOnly = true)
    public String decideRateSel(String receivedRateSel, String receivedSiteId) {
        Agency agency = agencyRepository.find(SiteId.of(receivedSiteId));
        AgencyKey agencyKey = agencyKeyRepository.find(agency.getAgencyId());
        List<String> activeProductList = agencyKey.getActiveProductList();
        List<Product> products = agencyKey.getProducts();
        AgencyPayment agencyPayment = Optional.ofNullable(agency.getAgencyPayment()).orElseGet(AgencyPayment::new);
        RateSel rateSel = agencyPayment.getRateSel();
        return paymentDomainService.decideRateSel(receivedRateSel, rateSel, activeProductList, products);
    }

    @Transactional(readOnly = true)
    public String decideStartDate(String startDate, String siteId) {
        Agency agency = agencyRepository.find(SiteId.of(siteId));
        AgencyPayment agencyPayment = Optional.ofNullable(agency.getAgencyPayment()).orElseGet(AgencyPayment::new);
        LocalDate start = agencyPayment.getStartDate();
        LocalDate end = agencyPayment.getEndDate();
        String extensionStatus = agency.getExtensionStatus();
        return paymentDomainService.decideStartDate(startDate, start, end, extensionStatus);

    }

    @Transactional(readOnly = true)
    public void isScheduled(String siteId) {
        Agency agency = agencyRepository.find(SiteId.of(siteId));
        AgencyPayment agencyPayment = Optional.ofNullable(agency.getAgencyPayment()).orElseGet(AgencyPayment::new);
        if (agencyPayment.isScheduledRateSel()) {
            throw new NoExtensionException(EnumResultCode.Subscription);
        }
    }

    private List<Payment> payments(String siteId) {
        return paymentRepository.findBySiteId(SiteId.of(siteId)).stream()
                .filter(e -> e.getPaymentDetails().getTrTrace().equals(EnumTradeTrace.USED.getCode()))
                .filter(e -> e.getPaymentDetails().getExtraAmountStatus().equals(EnumExtraAmountStatus.PASS.getCode()))
                .sorted(Comparator.comparing(Payment::getRegDate).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public int excessCount(String siteId) {
        Payment payment = paymentDomainService.excessPayment(payments(siteId)).orElse(null);
        if (payment == null) {
            return 0;
        }
        String startDate = payment.getPaymentPeriod().getStartDate();
        String endDate = payment.getPaymentPeriod().getEndDate();
        List<StatDay> statDays = statDayRepository.findAllByFromDateBetweenAndId(startDate, endDate, SiteId.of(siteId));
        String billingBase = agencyKeyRepository.find(payment.getAgencyId()).getBillingBase();

        int excessCount = paymentDomainService.excessCount(payment, billingBase, statDays);

        Agency agency = agencyRepository.find(SiteId.of(siteId));
        AgencyPayment agencyPayment = agency.getAgencyPayment();
        agencyRepository.add(Agency.updateExcessCount(agency, agencyPayment, excessCount));

        int useCount = paymentDomainService.useCount(statDays, billingBase);

        PaymentDetails paymentDetails = payment.getPaymentDetails();
        Payment updatedPayment = Payment.updatePaymentUseCount(payment, paymentDetails, useCount);
        paymentRepository.add(updatedPayment);

        return excessCount;
    }

    @Transactional(readOnly = true)
    public int excessAmount(String siteId) {
        Payment excessPayment = paymentDomainService.excessPayment(payments(siteId)).orElse(null);
        if (excessPayment == null) {
            return 0;
        }
        String startDate = excessPayment.getPaymentPeriod().getStartDate();
        String endDate = excessPayment.getPaymentPeriod().getEndDate();
        List<StatDay> statDays = statDayRepository.findAllByFromDateBetweenAndId(startDate, endDate, SiteId.of(siteId));
        String billingBase = agencyKeyRepository.find(excessPayment.getAgencyId()).getBillingBase();
        List<Product> products = agencyKeyRepository.find(excessPayment.getAgencyId()).getProducts();
        return paymentDomainService.excessAmount(excessPayment, products, billingBase, statDays);
    }

    @Transactional
    public List<Map<String, String>> listSel(String agencyId) {
        List<String> productList = agencyKeyRepository.find(AgencyId.of(agencyId)).getActiveProductList();
        return agencyKeyRepository.find(AgencyId.of(agencyId)).getProducts()
                .stream()
                .filter(e -> productList.contains(e.getId().toString()))
                .map(Product::toMap)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<String> clientInfo(String siteId) {
        return agencyRepository.find(SiteId.of(siteId)).getAgencyCompany().clientInfo();
    }

    @Transactional
    public boolean isExtendable(String siteId) {
        return agencyRepository.find(SiteId.of(siteId)).getExtensionStatus().equals(EnumExtensionStatus.EXTENDABLE.getCode());
    }


    @Transactional
    public void verifyValue(String agencyId, String siteId, String rateSel, String startDate, String endDate, String salesPrice, String offer) {
        List<Product> products = agencyKeyRepository.find(AgencyId.of(agencyId)).getProducts();
        if (!paymentDomainService.verifyValue(startDate, endDate, salesPrice, offer, products, rateSel, excessAmount(siteId), excessCount(siteId))) {
            throw new InvalidParameterException();
        }
    }


    @Override
    public void processPaymentCA(Map<String, String> responseParam, String agencyId, String siteId, String rateSel, String offer, LocalDate trDate, LocalDate startDate, LocalDate endDate, String billKey, String billKeyExpireDt) {
        PaymentRequest paymentRequest = ResponseParamMapper.mapToPaymentRequest(responseParam);
        paymentRepository.add(
                Payment.ofCA(
                        PGTradeNum.of(paymentRequest.getPgTradeNum()),
                        AgencyId.of(agencyId),
                        SiteId.of(siteId),
                        RateSel.of(rateSel),
                        PaymentDetails.builder()
                                .tradeNum(paymentRequest.getTradeNum())
                                .paymentType(paymentRequest.getMethod())
                                .amount(paymentRequest.getAmount())
                                .offer(offer)
                                .trTrace(EnumTradeTrace.USED.getCode())
                                .paymentStatus(EnumPaymentStatus.ACTIVE.getCode())
                                .trDate(trDate)
                                .extraAmountStatus(EnumExtraAmountStatus.PASS.getCode())
                                .build()
                        , PaymentPeriod.builder()
                                .startDate(startDate)
                                .endDate(endDate)
                                .build()
                        , billKey
                        , billKeyExpireDt
                )
        );
    }

    @Override
    @Transactional
    public void processPaymentVA(Map<String, String> responseParam, String agencyId, String siteId, String rateSel, String offer, LocalDate trDate, LocalDate startDate, LocalDate endDate) {
        PaymentRequest paymentRequest = ResponseParamMapper.mapToPaymentRequest(responseParam);
        Payment payment = paymentRepository.find(PGTradeNum.of(paymentRequest.getPgTradeNum()));
        PaymentDetails paymentDetails = payment.getPaymentDetails();
        PaymentDetails newPaymentDetails = PaymentDetails.builder()
                .tradeNum(paymentRequest.getTradeNum())
                .paymentType(paymentRequest.getMethod())
                .amount(paymentRequest.getAmount())
                .trTrace(EnumTradeTrace.USED.getCode())
                .paymentStatus(EnumPaymentStatus.ACTIVE.getCode())
                .offer(offer)
                .extraAmountStatus(EnumExtraAmountStatus.PASS.getCode())
                .trDate(trDate)
                .build();
        if (!paymentDetails.equals(newPaymentDetails)) {
            logger.info("Payment details are different {} to {}", paymentDetails, newPaymentDetails);
        }

        paymentRepository.add(Payment.ofVA(payment, newPaymentDetails));

        Agency agency = agencyRepository.find(SiteId.of(siteId));
        AgencyPayment agencyPayment = agency.getAgencyPayment();

        if (EnumExtensionStatus.DEFAULT.getCode().equals(agency.getExtensionStatus())) {
            agencyPayment = AgencyPayment.updateAgencyPayment(agencyPayment, RateSel.of(rateSel), startDate, endDate);
        }

        agencyRepository.add(
                Agency.updateAgencyByVAPending(
                        agency,
                        EnumSiteStatus.ACTIVE.getCode(),
                        EnumExtensionStatus.NOT_EXTENDABLE.getCode(),
                        agencyPayment,
                        RateSel.of(rateSel),
                        startDate,
                        endDate)
        );
    }


    @Override
    @Transactional
    public void processPaymentVAPending(Map<String, String> responseParam, String agencyId, String siteId, String rateSel, String offer, LocalDate trDate, LocalDate startDate, LocalDate endDate, LocalDate vBankExpireDate) {
        PaymentRequest paymentRequest = ResponseParamMapper.mapToPaymentRequest(responseParam);
        paymentRepository.add(
                Payment.ofVAPending(
                        PGTradeNum.of(paymentRequest.getPgTradeNum()),
                        AgencyId.of(agencyId),
                        SiteId.of(siteId),
                        RateSel.of(rateSel),
                        PaymentDetails.builder()
                                .tradeNum(paymentRequest.getTradeNum())
                                .paymentType(paymentRequest.getMethod())
                                .amount(paymentRequest.getAmount())
                                .offer(offer)
                                .trTrace(EnumTradeTrace.NOT_USED.getCode())
                                .paymentStatus(EnumPaymentStatus.NOT_DEPOSITED.getCode())
                                .trDate(trDate)
                                .extraAmountStatus(EnumExtraAmountStatus.PASS.getCode())
                                .build()
                        , PaymentPeriod.builder()
                                .startDate(startDate)
                                .endDate(endDate)
                                .build()
                        , VBank.builder()
                                .vBankName(paymentRequest.getVBankName())
                                .vBankAccount(paymentRequest.getVBankAccount())
                                .vBankExpireDate(vBankExpireDate)
                                .rcptName(paymentRequest.getRcptName())
                                .build()
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCurrentPayment(String pgTradeNum) {
        return paymentRepository.find(PGTradeNum.of(pgTradeNum)) == null;
    }
}
