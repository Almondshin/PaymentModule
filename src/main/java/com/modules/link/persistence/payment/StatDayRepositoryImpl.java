package com.modules.link.persistence.payment;

import com.modules.base.jpa.BaseRepository;
import com.modules.link.domain.agency.SiteId;
import com.modules.link.domain.payment.StatDay;
import com.modules.link.domain.payment.StatDayRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class StatDayRepositoryImpl extends BaseRepository<StatDay, SiteId, StatDayJpaRepository> implements StatDayRepository {
    public StatDayRepositoryImpl(StatDayJpaRepository repository) {
        super(repository);
    }

    @Transactional
    @Override
    public List<StatDay> findAllByFromDateBetweenAndId(String startDate, String endDate, SiteId siteId) {
        return repository.findAllByFromDateBetweenAndId(startDate, endDate, siteId);
    }

}
