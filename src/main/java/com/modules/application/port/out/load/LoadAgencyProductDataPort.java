package com.modules.application.port.out.load;

import com.modules.application.domain.AgencyProducts;

public interface LoadAgencyProductDataPort {
    AgencyProducts getAgencyProductByRateSel(String rateSel);
}
