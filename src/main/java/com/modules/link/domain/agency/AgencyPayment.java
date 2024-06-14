package com.modules.link.domain.agency;

import com.modules.base.domain.ValueObject;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

@Embeddable
@NoArgsConstructor
public class AgencyPayment extends ValueObject<AgencyPayment> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String AUTO_PAY = "autopay";
    @Getter
    @Column(name = "RATE_SEL")
    private String rateSel;
    @Column(name = "SCHEDULED_RATE_SEL")
    private String scheduledRateSel;
    @Column(name = "EXCESS_COUNT")
    private String excessCount;
    @Column(name = "START_DATE")
    private Date startDate;
    @Column(name = "END_DATE")
    private Date endDate;

    @Override
    public Object[] getEqualityFields() {
        return new Object[]{rateSel, scheduledRateSel, excessCount, startDate, endDate};
    }

    public boolean isScheduledRateSel() {
        return this.scheduledRateSel != null && this.scheduledRateSel.toLowerCase().contains(AUTO_PAY);
    }

    public Optional<LocalDate> getStartDate() {
        return Optional.ofNullable(startDate).map(date -> LocalDate.parse(date.toString(), DATE_FORMATTER));
    }

    public Optional<LocalDate> getEndDate() {
        return Optional.ofNullable(endDate).map(date -> LocalDate.parse(date.toString(), DATE_FORMATTER));
    }

    @Builder
    public AgencyPayment(String rateSel, String scheduledRateSel, String excessCount, Date startDate, Date endDate) {
        this.rateSel = rateSel;
        this.scheduledRateSel = scheduledRateSel;
        this.excessCount = excessCount;
        this.startDate = startDate;
        this.endDate = endDate;
    }


}
