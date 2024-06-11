package com.modules.link.service.payment;

import com.modules.link.domain.payment.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ProductRepository productRepository;


//    @Transactional
//    public Agency getAgency(SiteId siteId){
//        return productRepository.find(siteId);
//    }
//
//    @Transactional(readOnly = true)
//    public Agency getAgencyByPaymentId(PGTradeNum paymentId) {
//        Payment payment = paymentRepository.findById(paymentId)
//                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id: " + paymentId));
//        return agencyRepository.findById(payment.getAgencyId())
//                .orElseThrow(() -> new IllegalArgumentException("Agency not found with id: " + payment.getAgencyId()));
//    }
//
//    @Transactional(readOnly = true)
//    public List<Product> getProductListByPaymentId(PGTradeNum paymentId) {
//        Payment payment = paymentRepository.findById(paymentId)
//                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id: " + paymentId));
//        return payment.getProducts();
//    }


}
