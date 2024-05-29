package com.modules.link.persistence.agency;

import com.modules.base.jpa.BaseRepository;
import com.modules.link.domain.agency.Agency;
import com.modules.link.domain.agency.AgencyRepository;
import com.modules.link.domain.agency.SiteId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AgencyRepositoryImpl extends BaseRepository<Agency, SiteId, AgencyJpaRepository> implements AgencyRepository {

    public AgencyRepositoryImpl(AgencyJpaRepository repository) {super(repository);}

    @Transactional
    public Agency find(SiteId id) {
        return repository.findBySiteId(id);
    }

//    private final AgencyRepository agencyRepository;
//    private final AgencyKeyRepository agencyKeyRepository;
//
//    public AgencyRepositoryImpl(AgencyRepository agencyRepository, AgencyKeyRepository agencyKeyRepository) {
//        this.agencyRepository = agencyRepository;
//        this.agencyKeyRepository = agencyKeyRepository;
//    }
//
//    @Transactional
//    public AgencyKey getAgencyKey(String agencyId) {
//        return agencyKeyRepository.findById(agencyId)
//                .orElseThrow(() -> new IllegalStateException("'" + agencyId + "'의 제휴사는 존재하지 않는 제휴사 ID입니다."));
//    }
//
//    @Transactional
//    public Agency getAgencyBySiteId(SiteId siteId) {
//        return agencyRepository.findById(siteId)
//                .orElseThrow(() -> new IllegalStateException("'" + siteId + "'의 이용기관은 존재하지 않는 사이트 ID입니다."));
//    }
//
//    @Transactional
//    public void save(Agency agency) {
//        agencyRepository.save(agency);
//    }

}
