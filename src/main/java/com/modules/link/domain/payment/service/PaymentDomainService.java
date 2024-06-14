package com.modules.link.domain.payment.service;

import com.modules.link.domain.exception.NoExtensionException;
import com.modules.link.domain.payment.Payment;
import com.modules.link.domain.payment.Product;
import com.modules.link.domain.payment.StatDay;
import com.modules.link.enums.EnumBillingBase;
import com.modules.link.enums.EnumExtensionStatus;
import com.modules.link.enums.EnumResultCode;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
public class PaymentDomainService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String decideRateSel(String receivedRateSel, String existingRateSel) {
        return receivedRateSel != null && !receivedRateSel.isEmpty() ? receivedRateSel : existingRateSel;
    }

    public String decideStartDate(String receivedStartDate, LocalDate existingStartDate, LocalDate existingEndDate, String extensionStatus) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = makeLocalDate(receivedStartDate);

        if (extensionStatus.equals(EnumExtensionStatus.DEFAULT.getCode())) {
            if (existingStartDate != null) {
                return existingStartDate.format(DATE_FORMATTER);
            }
        }
        if (extensionStatus.equals(EnumExtensionStatus.EXTENDABLE.getCode())) {
            LocalDate fifteenDaysBeforeExpiration = existingEndDate.minusDays(15);
            if (receivedStartDate == null) {
                return now.format(DATE_FORMATTER);
            }
            if (startDate.isBefore(fifteenDaysBeforeExpiration)) {
                throw new NoExtensionException(EnumResultCode.NoExtension);
            }
            return receivedStartDate;
        }
        if (extensionStatus.equals(EnumExtensionStatus.NOT_EXTENDABLE.getCode())) {
            throw new NoExtensionException(EnumResultCode.NoExtension);
        }

        throw new RuntimeException("Invalid extension status: " + extensionStatus);
    }

    private LocalDate makeLocalDate(String date) {
        return LocalDate.parse(date, DATE_FORMATTER);
    }


    public Optional<Payment> excessPayment(List<Payment> payment) {
        if (payment.size() < 2) {
            return Optional.empty();
        }
        return Optional.of(payment.get(1));
    }


    public int excessCount(Payment payment, String billingBase, List<StatDay> statDays) {
        int offer = Integer.parseInt(payment.getPaymentDetails().getOffer());
        int excessCount = offer - useCount(statDays, billingBase);
        return excessCount < 0 ? Math.abs(excessCount) : 0;
    }

    public int excessAmount(Payment payment, Product product, String billingBase, List<StatDay> statDays) {
        int offer = Integer.parseInt(payment.getPaymentDetails().getOffer());
        int excessCount = offer - useCount(statDays, billingBase);
        int excessPerCase = Integer.parseInt(product.getExcessPerCase());
        return excessCount > 0 ? (int) Math.floor(excessCount * excessPerCase * 1.1) : 0;
    }

    private int useCount(List<StatDay> statDays, String billingBase) {
        if (billingBase.equals(EnumBillingBase.INCOMPLETE.getCode())) {
            return statDays.stream().mapToInt(StatDay::getIncompleteCount).sum();
        }
        if (billingBase.equals(EnumBillingBase.SUCCESS_FINAL.getCode())) {
            return statDays.stream().mapToInt(StatDay::getSuccessFinalCount).sum();
        }
        return 0;
    }

}