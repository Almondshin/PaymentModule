package com.modules.payment.application.port.out.load;

import com.modules.payment.application.domain.StatDay;

import java.util.List;


public interface LoadStatDataPort {
    List<StatDay> findBySiteIdAndFromDate(String siteId, String toDate, String fromDate);
}
