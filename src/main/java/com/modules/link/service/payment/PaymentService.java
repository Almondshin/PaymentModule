package com.modules.link.service.payment;

import com.modules.link.domain.agency.*;
import com.modules.link.domain.exception.NoExtensionException;
import com.modules.link.domain.payment.Payment;
import com.modules.link.domain.payment.PaymentRepository;
import com.modules.link.domain.payment.Product;
import com.modules.link.domain.payment.StatDay;
import com.modules.link.domain.payment.service.PaymentDomainService;
import com.modules.link.enums.EnumExtraAmountStatus;
import com.modules.link.enums.EnumResultCode;
import com.modules.link.enums.EnumTradeTrace;
import com.modules.link.service.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PaymentService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;
    private final AgencyRepository agencyRepository;
    private final PaymentDomainService paymentDomainService;

    @Transactional(readOnly = true)
    public void isSite(String siteId) {
        if (agencyRepository.find(SiteId.of(siteId)) == null || agencyRepository.findSite(SiteId.of(siteId)) == null) {
            throw new EntityNotFoundException(EnumResultCode.UnregisteredAgency, siteId);
        }
    }

    @Transactional(readOnly = true)
    public String decideRateSel(String receivedRateSel, String receivedSiteId) {
        Agency agency = agencyRepository.find(SiteId.of(receivedSiteId));
        return paymentDomainService.decideRateSel(receivedRateSel, agency.getAgencyPayment().getRateSel());
    }


    @Transactional(readOnly = true)
    public Optional<String> decideStartDate(String startDate, String siteId) {
        if (startDate != null && !startDate.isEmpty()) {
            return Optional.of(startDate);
        }

        Agency agency = agencyRepository.find(SiteId.of(siteId));
        AgencyPayment agencyPayment = agency.getAgencyPayment();
        LocalDate start = agencyPayment.getStartDate().orElse(null);
        LocalDate end = agencyPayment.getEndDate().orElse(null);

        if (start == null) {
            return Optional.empty();
        }

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
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public int excessAmount(String siteId) {
        Payment excessPayment = paymentDomainService.excessPayment(payments(siteId)).orElse(null);


        if (excessPayment == null) {
            return 0;
        }
        System.out.println("excessPayment.getPaymentDetails().getRateSel() : " + excessPayment.getRateSel());

        String startDate = excessPayment.getPaymentPeriod().getStartDate();
        String endDate = excessPayment.getPaymentPeriod().getEndDate();
        List<StatDay> statDays = paymentRepository.findAllByFromDateBetweenAndId(startDate, endDate, SiteId.of(siteId));
        AgencyKey agencyKey = agencyRepository.findAgencyKey(excessPayment.getAgencyId());
        String billingBase = agencyKey.getBillingBase();
        List<Product> products = excessPayment.getProducts();

        System.out.println("Products: " + products);

        for (Product product : products) {
            System.out.println("Product ID: " + product.getId() + ", Name: " + product.getName());
        }

        System.out.println("excessPayment.getAgency() : " + excessPayment.getAgency().getId().toString());
        return paymentDomainService.excessAmount(excessPayment, billingBase, statDays);
    }


//    @Transactional(readOnly = true)
//    public Agency getAgencyByPaymentId(PGTradeNum paymentId) {
//        Payment payment = paymentRepository.findById(paymentId)
//                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id: " + paymentId));
//        return agencyRepository.findById(payment.getAgencyId())
//                .orElseThrow(() -> new IllegalArgumentException("Agency not found with id: " + payment.getAgencyId()));
//    }
//
//    @Transactional(readOnly = true)
//    public List<Product> getProductListByPaymentId(PGTradeNum paymentId) {
//        Payment payment = paymentRepository.findById(paymentId)
//                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id: " + paymentId));
//        return payment.getProducts();
//    }


}
