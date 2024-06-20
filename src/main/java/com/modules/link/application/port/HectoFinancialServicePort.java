package com.modules.link.application.port;

import com.modules.link.domain.agency.Agency;

import java.util.Date;
import java.util.Map;

public interface HectoFinancialServicePort {
    void processPaymentCA(Map<String, String> responseParam, String agencyId, String siteId, String rateSel, String offer, Date trDate, Date startDate, Date endDate, String billKey) ;
    void processPaymentVA(Map<String, String> responseParam, String agencyId, String siteId, String rateSel, String offer, Date trDate, Date startDate, Date endDate, Date vBankExpireDate);
    void updateAgencyStatus(Agency agency);
    boolean isEmptyPayment();
}
