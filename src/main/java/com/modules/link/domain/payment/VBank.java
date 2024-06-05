package com.modules.link.domain.payment;

import com.modules.base.domain.ValueObject;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Date;

@Embeddable
@NoArgsConstructor
public class VBank extends ValueObject<VBank> {

    @Column(name = "VBANK_NAME")
    private String vbankName;
    @Column(name = "VBANK_CODE")
    private String vbankCode;
    @Column(name = "VBANK_ACCOUNT")
    private String vbankAccount;
    @Column(name = "VBANK_EXPIRE_DATE")
    private Date vbankExpireDate;
    @Column(name = "RCPT_NAME")
    private String rcptName;


    @Override
    protected Object[] getEqualityFields() {
        return new Object[]{vbankName, vbankCode, vbankAccount, vbankExpireDate, rcptName};
    }

    @Builder
    public VBank(String vbankName, String vbankCode, String vbankAccount, Date vbankExpireDate, String rcptName) {
        this.vbankName = vbankName;
        this.vbankCode = vbankCode;
        this.vbankAccount = vbankAccount;
        this.vbankExpireDate = vbankExpireDate;
        this.rcptName = rcptName;
    }
}
