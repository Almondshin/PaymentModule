package com.modules.payment.application.port.in;


import com.modules.payment.application.domain.StatDay;

import java.util.List;

public interface StatUseCase {
    List<StatDay> getUseCountBySiteId(String siteId, String startDate, String endDate);
}
