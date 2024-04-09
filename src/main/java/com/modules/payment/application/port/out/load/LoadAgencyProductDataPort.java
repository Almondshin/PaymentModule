package com.modules.payment.application.port.out.load;

import com.modules.payment.application.domain.AgencyProducts;

public interface LoadAgencyProductDataPort {
    AgencyProducts getAgencyProductByRateSel(String rateSel);
}
