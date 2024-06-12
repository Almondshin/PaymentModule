package com.modules.link.domain.agency;

import com.modules.base.domain.ValueObject;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;

@Embeddable
@NoArgsConstructor
public class AgencyManager extends ValueObject<AgencyManager> {
    @Column(name = "SETTLE_MANAGER_NAME")
    @NotBlank(message = "settleManagerName")
    private String name;

    @Column(name = "SETTLE_MANAGER_PHONE_NUMBER")
    @NotBlank(message = "settleManagerPhoneNumber")
    private String phoneNumber;

    @Column(name = "SETTLE_MANAGER_TEL_NUMBER")
    @NotBlank(message = "settleManagerTelNumber")
    private String telNumber;

    @Column(name = "SETTLE_MANAGER_EMAIL")
    @NotBlank(message = "settleManagerEmail")
    private String email;

    @Override
    protected Object[] getEqualityFields() { return new Object[] {name, phoneNumber, telNumber, email}; }

    @Builder
    public AgencyManager(String name, String phoneNumber, String telNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.telNumber = telNumber;
        this.email = email;
    }

}
