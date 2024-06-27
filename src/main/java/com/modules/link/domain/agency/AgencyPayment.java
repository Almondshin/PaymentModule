package com.modules.link.domain.agency;

import com.modules.base.domain.ValueObject;
import com.modules.link.domain.payment.RateSel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Embeddable
@NoArgsConstructor
public class AgencyPayment extends ValueObject<AgencyPayment> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
    private static final String AUTO_PAY = "autopay";

    @Type(type = "com.modules.link.domain.payment.RateSel$RateSelJavaType")
    @Column(name = "RATE_SEL")
    private RateSel rateSel;
    @Column(name = "SCHEDULED_RATE_SEL")
    private String scheduledRateSel;
    @Column(name = "EXCESS_COUNT")
    private String excessCount;
    @Column(name = "START_DATE")
    private LocalDate startDate;
    @Column(name = "END_DATE")
    private LocalDate endDate;

    @Override
    public Object[] getEqualityFields() {
        return new Object[]{rateSel, scheduledRateSel, excessCount, startDate, endDate};
    }

    public boolean isScheduledRateSel() {
        return this.scheduledRateSel != null && this.scheduledRateSel.toLowerCase().contains(AUTO_PAY);
    }

//    public Optional<LocalDate> getStartDate() {
//        return Optional.ofNullable(startDate).map(date -> LocalDate.parse(date.toString(), DATE_FORMATTER));
//    }
//
//    public Optional<LocalDate> getEndDate() {
//        return Optional.ofNullable(endDate).map(date -> LocalDate.parse(date.toString(), DATE_FORMATTER));
//    }

//    @Builder
//    public AgencyPayment(RateSel rateSel, String scheduledRateSel, String excessCount, Date startDate, Date endDate) {
//        this.rateSel = rateSel;
//        this.scheduledRateSel = scheduledRateSel;
//        this.excessCount = excessCount;
//        this.startDate = startDate;
//        this.endDate = endDate;
//    }

    @Builder
    public AgencyPayment(RateSel rateSel, String scheduledRateSel, String excessCount, LocalDate startDate, LocalDate endDate) {
        this.rateSel = rateSel;
        this.scheduledRateSel = scheduledRateSel;
        this.excessCount = excessCount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static AgencyPayment updateAgencyPayment(AgencyPayment agencyPayment, RateSel rateSel, LocalDate startDate, LocalDate endDate) {
        return AgencyPayment.builder()
                .rateSel(rateSel)
                .scheduledRateSel(agencyPayment.getScheduledRateSel())
                .excessCount(agencyPayment.getExcessCount())
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    public static AgencyPayment updateAgencyExcessCount(AgencyPayment agencyPayment, String excessCount) {
        return AgencyPayment.builder()
                .rateSel(agencyPayment.getRateSel())
                .scheduledRateSel(agencyPayment.getScheduledRateSel())
                .excessCount(excessCount)
                .startDate(agencyPayment.getStartDate())
                .endDate(agencyPayment.getEndDate())
                .build();
    }
}
