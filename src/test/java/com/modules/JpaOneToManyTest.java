package com.modules;


import com.modules.link.domain.agency.AgencyId;
import com.modules.link.domain.agency.AgencyKeyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


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
        System.out.println(agencyKeyRepository.find(aid).getProducts().toString());
        System.out.println("test");
    }

}
