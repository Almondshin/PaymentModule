package com.modules.application.port.in;


import com.modules.application.domain.StatDay;

import java.util.List;

public interface StatUseCase {
    List<StatDay> getUseCountBySiteId(String siteId, String startDate, String endDate);
}
