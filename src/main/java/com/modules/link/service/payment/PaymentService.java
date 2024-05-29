package com.modules.link.service.payment;

import com.modules.link.domain.payment.ProductRepository;
import com.modules.link.domain.payment.Products;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ProductRepository productRepository;

    @Transactional
    public List<Products> findAll() {
        return (List<Products>) productRepository.findAll();
    }

}
