package com.modules.link.domain.agency;

import com.modules.base.domain.ValueObject;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor
public class Manager extends ValueObject<Manager> {
    @Column(name = "SETTLE_MANAGER_NAME")
    private String name;

    @Column(name = "SETTLE_MANAGER_PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "SETTLE_MANAGER_TEL_NUMBER")
    private String telNumber;

    @Column(name = "SETTLE_MANAGER_EMAIL")
    private String email;

    @Override
    protected Object[] getEqualityFields() { return new Object[] {name, phoneNumber, telNumber, email}; }

    @Builder
    public Manager(String name, String phoneNumber, String telNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.telNumber = telNumber;
        this.email = email;
    }

}
