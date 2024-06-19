package com.modules.link.domain.payment.service;

import com.modules.link.domain.payment.Payment;
import com.modules.link.domain.payment.Product;
import com.modules.link.domain.payment.StatDay;
import com.modules.link.enums.EnumBillingBase;
import com.modules.link.enums.EnumExtensionStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class PaymentDomainService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Optional<String> decideRateSel(String receivedRateSel, String existingRateSel, List<Product> products) {
        if (receivedRateSel != null && !receivedRateSel.isEmpty()) {
            return products.stream()
                    .filter(e -> e.getId().toString().equals(receivedRateSel))
                    .findFirst().map(product -> product.getId().toString());
        }

        return Optional.ofNullable(existingRateSel);
    }
    public Optional<String> decideStartDate(String receivedStartDate, LocalDate existingStartDate, LocalDate existingEndDate, String extensionStatus) {
        LocalDate now = LocalDate.now();
        LocalDate startDate;
        if (receivedStartDate != null && !receivedStartDate.isEmpty()) {
            startDate = makeLocalDate(receivedStartDate);
            if (extensionStatus.equals(EnumExtensionStatus.DEFAULT.getCode())) {
                if (startDate.isBefore(now)) {
                    return Optional.empty();
                }
            }
            if (extensionStatus.equals(EnumExtensionStatus.EXTENDABLE.getCode())) {
                LocalDate fifteenDaysBeforeExpiration = existingEndDate.minusDays(15);
                if (startDate.isBefore(fifteenDaysBeforeExpiration)) {
                    return Optional.empty();
                }
                return Optional.of(receivedStartDate);
            }
            if (extensionStatus.equals(EnumExtensionStatus.NOT_EXTENDABLE.getCode())) {
                return Optional.empty();
            }
        }
        return Optional.of(Objects.requireNonNullElse(existingStartDate, now).format(DATE_FORMATTER));
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
        return excessCount < 0 ? (int) Math.abs(Math.round(excessCount * excessPerCase * 1.1)) : 0;
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

    public boolean verifyValue(String startDateStr, String endDateStr, String salesPrice, String offer, Product product, int excessAmount, int excessCount) {
        LocalDate startDate = makeLocalDate(startDateStr);
        LocalDate endDate = makeLocalDate(endDateStr);
        return !isValidStartDate(startDate) || !isValidEndDate(startDate, endDate) || !isValidPriceAndAmount(startDate, product, salesPrice, offer, excessAmount, excessCount);
    }

    private boolean isValidStartDate(LocalDate startDate) {
        LocalDate now = LocalDate.now();
        return !startDate.isBefore(now);
    }

    private boolean isValidEndDate(LocalDate startDate, LocalDate endDate) {
        LocalDate validDate = startDate.withDayOfMonth(startDate.lengthOfMonth()).minusDays(15);
        if (startDate.isBefore(validDate)) {
            return endDate.equals(startDate.withDayOfMonth(startDate.lengthOfMonth()));
        } else {
            return endDate.equals(startDate.plusMonths(1).withDayOfMonth(startDate.plusMonths(1).lengthOfMonth()));
        }
    }

    private boolean isValidPriceAndAmount(LocalDate startDate, Product product, String salesPrice, String offer, int excessAmount, int excessCount) {
        int lastDate = startDate.withDayOfMonth(startDate.lengthOfMonth()).getDayOfMonth();
        int startDay = startDate.getDayOfMonth();
        int durations = lastDate - startDay + 1;
        int baseOffer = Integer.parseInt(product.getOffer()) / Integer.parseInt(product.getMonth());
        int basePrice = Integer.parseInt(product.getPrice()) / Integer.parseInt(product.getMonth());
        int month = Integer.parseInt(product.getMonth());
        int calcOffer;
        double calcPrice;

        if (month == 1) {
            if (durations <= 14) {
                calcOffer = baseOffer + (baseOffer * durations / lastDate);
                calcPrice = ((double) (basePrice * durations) / lastDate + basePrice) * 1.1;
            } else {
                calcOffer = baseOffer * durations / lastDate;
                calcPrice = ((double) (basePrice * durations) / lastDate) * 1.1;
            }
        } else {
            calcOffer = baseOffer * (month - 1) + baseOffer * durations / lastDate;
            calcPrice = ((double) (basePrice * durations) / lastDate + basePrice * (month - 1)) * 1.1;
        }

        calcPrice += excessAmount;
        calcOffer += excessCount;

        System.out.println("calcPrice: " + Math.floor(calcPrice));
        System.out.println("calcOffer: " + calcOffer);

        return String.valueOf(calcOffer).equals(offer) && String.valueOf(Math.floor(calcPrice)).equals(salesPrice);
    }


}