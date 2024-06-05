package com.modules.link.domain.payment;

import com.modules.base.domain.ValueObject;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Date;

@Embeddable
@NoArgsConstructor
public class PaymentPeriod extends ValueObject<PaymentPeriod> {

    @Column(name = "START_DATE")
    private Date startDate;
    @Column(name = "END_DATE")
    private Date endDate;
    @Column(name = "BILL_KEY_EXPIRE_DATE")
    private Date billKeyExpireDate;

    @Builder
    public PaymentPeriod(Date startDate, Date endDate, Date billKeyExpireDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.billKeyExpireDate = billKeyExpireDate;
    }

    @Override
    protected Object[] getEqualityFields() {
        return new Object[]{startDate, endDate, billKeyExpireDate};
    }
}