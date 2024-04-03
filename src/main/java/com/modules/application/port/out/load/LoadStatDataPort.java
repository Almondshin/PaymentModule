package com.modules.application.port.out.load;

import com.modules.application.domain.StatDay;

import java.util.List;


public interface LoadStatDataPort {
    List<StatDay> findBySiteIdAndFromDate(String siteId, String toDate, String fromDate);
}
