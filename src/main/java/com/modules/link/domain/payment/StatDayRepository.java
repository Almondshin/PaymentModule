package com.modules.link.domain.payment;

import com.modules.base.domain.Repository;
import com.modules.link.domain.agency.SiteId;

import java.util.List;

public interface StatDayRepository extends Repository<StatDay, SiteId> {
    List<StatDay> findAllByFromDateBetweenAndId(String startDate, String endDate, SiteId siteId);
}
