package com.modules.payment.application.port.out.load;


import com.modules.payment.domain.Product;

import java.util.Optional;

public interface LoadAgencyProductDataPort {
    Optional<Product> getAgencyProductByRateSel(String rateSel);
}
