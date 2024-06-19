package com.modules.link.service.payment;

import com.modules.link.domain.agency.*;
import com.modules.link.domain.exception.NoExtensionException;
import com.modules.link.domain.payment.*;
import com.modules.link.domain.payment.service.PaymentDomainService;
import com.modules.link.enums.EnumExtensionStatus;
import com.modules.link.enums.EnumExtraAmountStatus;
import com.modules.link.enums.EnumResultCode;
import com.modules.link.enums.EnumTradeTrace;
import com.modules.link.service.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PaymentService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final AgencyRepository agencyRepository;
    private final PaymentDomainService paymentDomainService;

    @Transactional(readOnly = true)
    public void isValidSite(String siteId) {
        if (agencyRepository.find(SiteId.of(siteId)) == null || agencyRepository.findSite(SiteId.of(siteId)) == null) {
            throw new EntityNotFoundException(EnumResultCode.UnregisteredAgency, siteId);
        }
    }

    @Transactional(readOnly = true)
    public String decideRateSel(String receivedRateSel, String receivedSiteId) {
        Agency agency = agencyRepository.find(SiteId.of(receivedSiteId));
        List<String> productList = agencyRepository.findAgencyKey(agency.getAgencyId()).getProductList();
        List<Product> products = productRepository.findByAgencyId(agency.getAgencyId())
                .stream()
                .filter(e -> productList.contains(e.getId().toString()))
                .collect(Collectors.toList());
        return paymentDomainService.decideRateSel(receivedRateSel, agency.getAgencyPayment().getRateSel(), products);
    }

    @Transactional(readOnly = true)
    public Optional<String> decideStartDate(String startDate, String siteId) {
        Agency agency = agencyRepository.find(SiteId.of(siteId));
        AgencyPayment agencyPayment = agency.getAgencyPayment();
        LocalDate start = agencyPayment.getStartDate().orElse(null);
        LocalDate end = agencyPayment.getEndDate().orElse(null);

        return Optional.ofNullable(paymentDomainService.decideStartDate(startDate, start, end, agency.getExtensionStatus()));
    }

    @Transactional(readOnly = true)
    public void isScheduled(String siteId) {
        Agency agency = agencyRepository.find(SiteId.of(siteId));
        if (agency.getAgencyPayment().isScheduledRateSel()) {
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
        Payment excessPayment = paymentDomainService.excessPayment(payments(siteId)).orElse(null);
        if (excessPayment == null) {
            return 0;
        }
        String startDate = excessPayment.getPaymentPeriod().getStartDate();
        String endDate = excessPayment.getPaymentPeriod().getEndDate();
        List<StatDay> statDays = paymentRepository.findAllByFromDateBetweenAndId(startDate, endDate, SiteId.of(siteId));
        String billingBase = agencyRepository.findAgencyKey(excessPayment.getAgencyId()).getBillingBase();

        return paymentDomainService.excessCount(excessPayment, billingBase, statDays);
    }

    @Transactional(readOnly = true)
    public int excessAmount(String siteId) {
        Payment excessPayment = paymentDomainService.excessPayment(payments(siteId)).orElse(null);
        if (excessPayment == null) {
            return 0;
        }
        String startDate = excessPayment.getPaymentPeriod().getStartDate();
        String endDate = excessPayment.getPaymentPeriod().getEndDate();
        List<StatDay> statDays = paymentRepository.findAllByFromDateBetweenAndId(startDate, endDate, SiteId.of(siteId));
        String billingBase = agencyRepository.findAgencyKey(excessPayment.getAgencyId()).getBillingBase();
        Product product = productRepository.find(excessPayment.getRateSel());
        if (product == null) {
            throw new IllegalArgumentException("Product not found with id: " + excessPayment.getRateSel());
        }
        return paymentDomainService.excessAmount(excessPayment, product, billingBase, statDays);
    }

    @Transactional
    public List<Map<String, String>> listSel(String agencyId) {
        List<String> productList = agencyRepository.findAgencyKey(AgencyId.of(agencyId)).getProductList();
        return productRepository.findByAgencyId(AgencyId.of(agencyId))
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
        Payment payment = paymentDomainService.excessPayment(payments(siteId)).orElse(null);
        if (payment == null) {
            return false;
        }
        return payment.getPaymentDetails().getExtraAmountStatus().equals(EnumExtensionStatus.EXTENDABLE.getCode());
    }


    @Transactional
    public void verifyValue(String siteId, String rateSel, String startDate, String endDate, String salesPrice, String offer) {
        Product product = productRepository.find(RateSel.of(rateSel));
        if (!paymentDomainService.verifyValue(startDate, endDate, salesPrice, offer, product, excessAmount(siteId), excessCount(siteId))) {
            throw new InvalidParameterException();
        }
    }


}
