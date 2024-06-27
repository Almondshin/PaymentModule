package com.modules;


import com.modules.link.domain.agency.AgencyId;
import com.modules.link.domain.agency.AgencyKey;
import com.modules.link.domain.agency.AgencyKeyRepository;
import com.modules.link.domain.payment.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
public class JpaOneToManyTest {

    private final AgencyKeyRepository agencyKeyRepository;

    @Autowired
    public JpaOneToManyTest(AgencyKeyRepository agencyKeyRepository) {
        this.agencyKeyRepository = agencyKeyRepository;
    }

    @Test
    public void test() {
        AgencyId aid = new AgencyId("squares");
//        System.out.println(agencyKeyRepository.find(aid).getProducts().toString());
        List<AgencyKey> agencyKeys=  agencyKeyRepository.findAll();
        System.out.println("===========================================================");
        for (AgencyKey agencyKey : agencyKeys) {
            System.out.println("agencyKey: " + agencyKey.getId());
            for (Product product : agencyKey.getProducts()) {
                System.out.println(" - " + product.getName());
            }
        }
        System.out.println("test");
    }

}
