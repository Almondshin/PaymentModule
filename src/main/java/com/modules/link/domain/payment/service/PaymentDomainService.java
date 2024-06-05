package com.modules.link.domain.payment.service;

import com.modules.link.controller.container.PaymentReceived;
import com.modules.link.domain.agency.Agency;
import com.modules.link.enums.EnumExtensionStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PaymentDomainService {


    public String decideRateSel(Agency agency, PaymentReceived receivedData) {
        String rateSel = receivedData.getRateSel() != null && !receivedData.getRateSel().isEmpty()
                ? receivedData.getRateSel()
                : agency.getAgencyPayment().getRateSel() != null
                ? agency.getAgencyPayment().getRateSel() : null;

        if (rateSel == null) {
            throw new RuntimeException("Rate selection is null.");
        }

        return rateSel;
    }

    public LocalDate decideStartDate(Agency agency, PaymentReceived receivedData) {
        LocalDate agencyStartDate = agency.getAgencyPayment().getStartDate();
        LocalDate receivedStartDate = receivedData.getStartDate();

        if (agency.getExtensionStatus().equals(EnumExtensionStatus.DEFAULT.getCode())) {
            if (agencyStartDate != null) {
                return agencyStartDate;
            }
            if (receivedStartDate != null) {
                return receivedStartDate;
            }
        }

        if (agency.getExtensionStatus().equals(EnumExtensionStatus.EXTENDABLE.getCode())) {
            LocalDate agencyEndDate = agency.getAgencyPayment().getEndDate();
            LocalDate fifteenDaysBeforeExpiration = agencyEndDate.minusDays(15);
            LocalDate yesterday = LocalDate.now().minusDays(1);

            if (receivedStartDate != null) {
                if (!receivedStartDate.isBefore(fifteenDaysBeforeExpiration) && !receivedStartDate.isBefore(yesterday)) {
                    return receivedStartDate;
                } else {
                    throw new RuntimeException("Invalid start date.");
                }
            } else {
                return agencyEndDate.plusDays(1);
            }
        }

        if (agency.getExtensionStatus().equals(EnumExtensionStatus.NOT_EXTENDABLE.getCode())) {
            //TODO
            // 연장 불가능 상태 입니다.
//            throw new NoExtensionException(EnumResultCode.NoExtension, clientInfo.getSiteId());
        }

        throw new RuntimeException("Unable to decide start date.");
    }
}