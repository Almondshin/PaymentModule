package com.modules.link.domain.payment;


import com.modules.base.domain.Repository;
import com.modules.link.domain.agency.SiteId;

import java.util.List;

public interface PaymentRepository extends Repository<Payment, PGTradeNum> {
    List<Payment> findBySiteId(SiteId siteId);

    List<StatDay> findAllByFromDateBetweenAndId(String startDate, String endDate, SiteId siteId);
}
