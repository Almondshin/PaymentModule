package com.modules.link.persistence.payment;

import com.modules.link.domain.agency.SiteId;
import com.modules.link.domain.payment.StatDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatDayJpaRepository extends JpaRepository<StatDay, SiteId> {
    List<StatDay> findAllByFromDateBetweenAndId(String startDate, String endDate, SiteId siteId);
}
