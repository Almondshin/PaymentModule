package com.modules.payment.adapter.out.persistence.adapter;

import com.modules.payment.adapter.out.persistence.entity.AgencyJpaEntity;
import com.modules.payment.adapter.out.persistence.entity.SiteInfoJpaEntity;
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
import java.util.ArrayList;
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
        Optional<AgencyJpaEntity> foundAgencyInfo = agencyRepository.findByAgencyIdAndSiteId(entity.getAgencyId(), entity.getSiteId());
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
        AgencyJpaEntity entity = agencyAndClientConvertToEntity(agency, client);
        Optional<SiteInfoJpaEntity> foundSiteIdBySiteInfo = siteInfoRepository.findBySiteId(entity.getSiteId());
        Optional<AgencyJpaEntity> foundSiteIdByAgencyInfo = agencyRepository.findBySiteId(entity.getSiteId());

        // SiteId가 이미 존재하는 경우 DuplicateMemberException을 발생시킵니다.
        if (foundSiteIdBySiteInfo.isPresent() || foundSiteIdByAgencyInfo.isPresent()) {
            throw new DuplicateMemberException(EnumResultCode.DuplicateMember, entity.getSiteId());
        }
        client.setSiteStatus(EnumSiteStatus.PENDING.getCode());
        client.setExtensionStatus(EnumExtensionStatus.DEFAULT.getCode());
        agencyRepository.save(convertToEntity(agency, client, settleManager));
    }

    @Override
    @Transactional
    public void updateAgency(Agency agency) {
        String siteId = agency.getAgencyId() + "-" + agency.getSiteId();
        Optional<AgencyJpaEntity> optionalEntity = agencyRepository.findByAgencyIdAndSiteId(agency.getAgencyId(), siteId);
        if (optionalEntity.isPresent()) {
            AgencyJpaEntity entity = optionalEntity.get();
            if (entity.getExtensionStatus().equals(EnumExtensionStatus.DEFAULT.getCode())) {
                if (!paymentStatus.equals(EnumPaymentStatus.NOT_DEPOSITED.getCode())){
                    entity.setStartDate(client.getStartDate());
                    entity.setEndDate(client.getEndDate());
                }
                entity.setRateSel(client.getRateSel());
            }
            if (client.getRateSel().toLowerCase().contains("autopay")){
                entity.setScheduledRateSel(client.getRateSel());
            }
            if (!paymentStatus.equals(EnumPaymentStatus.NOT_DEPOSITED.getCode())){
                entity.setExtensionStatus(EnumExtensionStatus.NOT_EXTENDABLE.getCode());
            }
        } else {
            throw new EntityNotFoundException("optionalEntity : " + agency.getAgencyId() + ", " + agency.getSiteId() + "인 엔터티를 찾을 수 없습니다.");
        }
    }

    @Override
    @Transactional
    public void updateAgencyExcessCount(Agency agency, int excessCount) {
        String siteId = agency.getAgencyId() + "-" + agency.getSiteId();
        Optional<AgencyJpaEntity> optionalEntity = agencyRepository.findByAgencyIdAndSiteId(agency.getAgencyId(),siteId);
        if (optionalEntity.isPresent()){
            AgencyJpaEntity entity = optionalEntity.get();
            entity.setExcessCount(Integer.toString(excessCount));
        } else {
            throw new EntityNotFoundException("optionalEntity : " + agency.getAgencyId() + ", " + agency.getSiteId() + "인 엔터티를 찾을 수 없습니다.");
        }
    }


    private AgencyJpaEntity agencyAndClientConvertToEntity(Agency agency) {
        AgencyJpaEntity entity = new AgencyJpaEntity();
        String siteId = agency.getAgencyId() + "-" + agency.getSiteId();
        entity.setAgencyId(agency.getAgencyId());
        entity.setSiteId(siteId);
        entity.setRateSel(agency.getRateSel());
        entity.setStartDate(agency.getStartDate());
        entity.setEndDate(agency.getEndDate());
        return entity;
    }

    private AgencyJpaEntity convertToEntity(Agency agency) {
        AgencyJpaEntity agencyJpaEntity = new AgencyJpaEntity();

        agencyJpaEntity.setAgencyId(agency.getAgencyId());
        String siteId = agency.getAgencyId() + "-" + agency.getSiteId();
        agencyJpaEntity.setSiteId(siteId);

        agencyJpaEntity.setSiteName(client.getSiteName());
        agencyJpaEntity.setCompanyName(client.getCompanyName());
        agencyJpaEntity.setBusinessType(client.getBusinessType());
        agencyJpaEntity.setBizNumber(client.getBizNumber());
        agencyJpaEntity.setCeoName(client.getCeoName());
        agencyJpaEntity.setPhoneNumber(client.getPhoneNumber());
        agencyJpaEntity.setAddress(client.getAddress());
        agencyJpaEntity.setCompanySite(client.getCompanySite());
        agencyJpaEntity.setEmail(client.getEmail());
        agencyJpaEntity.setRateSel(client.getRateSel());
        agencyJpaEntity.setSiteStatus(client.getSiteStatus());
        agencyJpaEntity.setExtensionStatus(client.getExtensionStatus());
        agencyJpaEntity.setStartDate(client.getStartDate());
        agencyJpaEntity.setEndDate(client.getEndDate());

        agencyJpaEntity.setServiceUseAgree(client.getServiceUseAgree());

        agencyJpaEntity.setSettleManagerName(settleManager.getSettleManagerName());
        agencyJpaEntity.setSettleManagerTelNumber(settleManager.getSettleManagerTelNumber());
        agencyJpaEntity.setSettleManagerPhoneNumber(settleManager.getSettleManagerPhoneNumber());
        agencyJpaEntity.setSettleManagerEmail(settleManager.getSettleManagerEmail());

        return agencyJpaEntity;
    }
}
