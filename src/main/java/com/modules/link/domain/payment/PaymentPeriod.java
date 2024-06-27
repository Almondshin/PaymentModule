package com.modules.link.domain.payment;

import com.modules.base.domain.ValueObject;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Embeddable
@NoArgsConstructor
public class PaymentPeriod extends ValueObject<PaymentPeriod> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    @Column(name = "START_DATE")
    private LocalDate startDate;
    @Column(name = "END_DATE")
    private LocalDate endDate;

    @Builder
    public PaymentPeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getStartDate() {
        return LocalDateTime.parse(startDate.toString(), DATE_TIME_FORMATTER).toLocalDate().format(DATE_FORMATTER);
    }
    public String getEndDate() {
        return LocalDateTime.parse(endDate.toString(), DATE_TIME_FORMATTER).toLocalDate().format(DATE_FORMATTER);
    }

    @Override
    protected Object[] getEqualityFields() {
        return new Object[]{startDate, endDate};
    }
}