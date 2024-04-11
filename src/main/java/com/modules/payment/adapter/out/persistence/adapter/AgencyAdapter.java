package com.modules.payment.adapter.out.persistence.adapter;

import com.modules.payment.domain.entity.AgencyJpaEntity;
import com.modules.payment.domain.entity.SiteInfoJpaEntity;
import com.modules.payment.adapter.out.persistence.repository.AgencyRepository;
import com.modules.payment.adapter.out.persistence.repository.SiteInfoRepository;
import com.modules.payment.application.enums.EnumExtensionStatus;
import com.modules.payment.application.enums.EnumPaymentStatus;
import com.modules.payment.application.enums.EnumSiteStatus;
import com.modules.payment.application.exceptions.enums.EnumResultCode;
import com.modules.payment.application.exceptions.exceptions.DuplicateMemberException;
import com.modules.payment.application.exceptions.exceptions.UnregisteredAgencyException;
import com.modules.payment.application.mapper.AgencyMapper;
import com.modules.payment.application.port.out.load.LoadAgencyDataPort;
import com.modules.payment.application.port.out.save.SaveAgencyDataPort;
import com.modules.payment.domain.Agency;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AgencyAdapter implements LoadAgencyDataPort, SaveAgencyDataPort {
    private final AgencyRepository agencyRepository;
    private final SiteInfoRepository siteInfoRepository;

    public AgencyAdapter(AgencyRepository agencyRepository, SiteInfoRepository siteInfoRepository) {
        this.agencyRepository = agencyRepository;
        this.siteInfoRepository = siteInfoRepository;
    }


    @Override
    @Transactional
    public Optional<Agency> getAgencyInfo(Agency agency) {
        AgencyJpaEntity entity = agency.toEntity();
        String siteId = entity.getAgencyId() + "-" + entity.getSiteId();
        Optional<AgencyJpaEntity> foundAgencyInfo = agencyRepository.findByAgencyIdAndSiteId(entity.getAgencyId(), siteId);
        if (foundAgencyInfo.isEmpty()) {
            throw new UnregisteredAgencyException(EnumResultCode.UnregisteredAgency);
        }
        return foundAgencyInfo.map(AgencyMapper::convertToAgency);
    }

    @Override
    public List<Agency> selectAgencyInfo() {
        return agencyRepository.findAll().stream()
                .map(AgencyMapper::convertToAgency)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void registerAgency(Agency agency) {
        AgencyJpaEntity entity = agency.toEntity();
        Optional<SiteInfoJpaEntity> foundSiteIdBySiteInfo = siteInfoRepository.findBySiteId(entity.getSiteId());
        Optional<AgencyJpaEntity> foundSiteIdByAgencyInfo = agencyRepository.findBySiteId(entity.getSiteId());

        // SiteId가 이미 존재하는 경우 DuplicateMemberException을 발생시킵니다.
        if (foundSiteIdBySiteInfo.isPresent() || foundSiteIdByAgencyInfo.isPresent()) {
            throw new DuplicateMemberException(EnumResultCode.DuplicateMember, entity.getSiteId());
        }
        entity.setSiteId(entity.getAgencyId() + "-" + entity.getSiteId());
        entity.setSiteStatus(EnumSiteStatus.PENDING.getCode());
        entity.setExtensionStatus(EnumExtensionStatus.DEFAULT.getCode());
        agencyRepository.save(entity);
    }

    @Override
    @Transactional
    public void updateAgency(Agency agency, String paymentStatus) {
        AgencyJpaEntity entity = agency.toEntity();
        String siteId = entity.getAgencyId() + "-" + entity.getSiteId();
        Optional<AgencyJpaEntity> optionalEntity = agencyRepository.findByAgencyIdAndSiteId(entity.getAgencyId(), siteId);
        if (optionalEntity.isPresent()) {
            AgencyJpaEntity searchedEntity = optionalEntity.get();
            if (searchedEntity.getExtensionStatus().equals(EnumExtensionStatus.DEFAULT.getCode())) {
                if (!paymentStatus.equals(EnumPaymentStatus.NOT_DEPOSITED.getCode())) {
                    searchedEntity.setStartDate(entity.getStartDate());
                    searchedEntity.setEndDate(entity.getEndDate());
                }
                searchedEntity.setRateSel(entity.getRateSel());
            }
            if (entity.getRateSel().toLowerCase().contains("autopay")) {
                searchedEntity.setScheduledRateSel(entity.getRateSel());
            }
            if (!paymentStatus.equals(EnumPaymentStatus.NOT_DEPOSITED.getCode())) {
                searchedEntity.setExtensionStatus(EnumExtensionStatus.NOT_EXTENDABLE.getCode());
            }
        } else {
            throw new EntityNotFoundException("optionalEntity : " + entity.getAgencyId() + ", " + entity.getSiteId() + "인 엔터티를 찾을 수 없습니다.");
        }
    }

    @Override
    @Transactional
    public void updateAgencyExcessCount(Agency agency, int excessCount) {
        AgencyJpaEntity entity = agency.toEntity();
        String siteId = entity.getAgencyId() + "-" + entity.getSiteId();
        Optional<AgencyJpaEntity> optionalEntity = agencyRepository.findByAgencyIdAndSiteId(entity.getAgencyId(), siteId);
        if (optionalEntity.isPresent()) {
            AgencyJpaEntity searchedEntity = optionalEntity.get();
            searchedEntity.setExcessCount(Integer.toString(excessCount));
        } else {
            throw new EntityNotFoundException("optionalEntity : " + entity.getAgencyId() + ", " + entity.getSiteId() + "인 엔터티를 찾을 수 없습니다.");
        }
    }
}
