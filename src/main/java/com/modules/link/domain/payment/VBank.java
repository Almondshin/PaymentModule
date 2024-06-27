package com.modules.link.domain.payment;

import com.modules.base.domain.ValueObject;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;
import java.util.Date;

@Embeddable
@NoArgsConstructor
public class VBank extends ValueObject<VBank> {

    @Column(name = "VBANK_NAME")
    private String vBankName;
    @Column(name = "VBANK_ACCOUNT")
    private String vBankAccount;
    @Column(name = "VBANK_EXPIREDATE")
    private LocalDate vBankExpireDate;
    @Column(name = "RCPT_NAME")
    private String rcptName;


    @Override
    protected Object[] getEqualityFields() {
        return new Object[]{vBankName, vBankAccount, vBankExpireDate, rcptName};
    }

    @Builder
    public VBank(String vBankName, String vBankAccount, LocalDate vBankExpireDate, String rcptName) {
        this.vBankName = vBankName;
        this.vBankAccount = vBankAccount;
        this.vBankExpireDate = vBankExpireDate;
        this.rcptName = rcptName;
    }
}
