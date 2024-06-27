package com.modules.link.controller.mapper;

import com.modules.link.controller.dto.AgencyDtos.RegisterInfo;
import com.modules.link.domain.agency.Agency;
import com.modules.link.domain.agency.AgencyCompany;
import com.modules.link.domain.agency.AgencyManager;
import com.modules.link.domain.agency.AgencyPayment;
import com.modules.link.domain.payment.RateSel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class AgencyMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public static Agency toAgency(RegisterInfo registerInfo) {
        LocalDate localDateStart = convertToLocalDate(registerInfo.getStartDate());
        return Agency.of(
                registerInfo.getSiteId(),
                registerInfo.getAgencyId(),
                AgencyCompany.builder()
                        .siteName(registerInfo.getSiteName())
                        .companyName(registerInfo.getCompanyName())
                        .businessType(registerInfo.getBusinessType())
                        .bizNumber(registerInfo.getBizNumber())
                        .ceo(registerInfo.getCeoName())
                        .phoneNumber(registerInfo.getPhoneNumber())
                        .address(registerInfo.getAddress())
                        .companySite(registerInfo.getCompanySite())
                        .email(registerInfo.getEmail())
                        .serviceUseAgree(registerInfo.getServiceUseAgree())
                        .build(),
                AgencyPayment.builder()
                        .rateSel(RateSel.of(registerInfo.getRateSel()))
                        .startDate(localDateStart)
                        .build(),
                AgencyManager.builder()
                        .name(registerInfo.getSettleManagerName())
                        .phoneNumber(registerInfo.getSettleManagerPhoneNumber())
                        .telNumber(registerInfo.getSettleManagerTelNumber())
                        .email(registerInfo.getSettleManagerEmail())
                        .build()
        );
    }

    private static LocalDate convertToLocalDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        return LocalDate.parse(date, DATE_TIME_FORMATTER);
    }
}
