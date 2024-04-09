package com.modules.payment.adapter.out.persistence.adapter;

import com.modules.payment.domain.Agency;
import com.modules.payment.adapter.out.persistence.entity.AgencyJpaEntity;
import com.modules.payment.adapter.out.persistence.entity.SiteInfoJpaEntity;
import com.modules.payment.adapter.out.persistence.repository.AgencyRepository;
import com.modules.payment.adapter.out.persistence.repository.SiteInfoRepository;
import com.modules.payment.application.domain.SettleManager;
import com.modules.payment.application.enums.EnumExtensionStatus;
import com.modules.payment.application.enums.EnumPaymentStatus;
import com.modules.payment.application.enums.EnumSiteStatus;
import com.modules.payment.application.exceptions.enums.EnumResultCode;
import com.modules.payment.application.exceptions.exceptions.DuplicateMemberException;
import com.modules.payment.application.exceptions.exceptions.UnregisteredAgencyException;
import com.modules.payment.application.port.out.load.LoadAgencyDataPort;
import com.modules.payment.application.port.out.save.SaveAgencyDataPort;
import com.modules.payment.application.service.NotiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AgencyAdapter implements LoadAgencyDataPort, SaveAgencyDataPort {
    private final AgencyRepository agencyRepository;
    private final SiteInfoRepository siteInfoRepository;

    private final NotiService notiService;
    @Value("${external.admin.url}")
    private String profileSpecificAdminUrl;

    public AgencyAdapter(AgencyRepository agencyRepository, SiteInfoRepository siteInfoRepository, NotiService notiService) {
        this.agencyRepository = agencyRepository;
        this.siteInfoRepository = siteInfoRepository;
        this.notiService = notiService;
    }


    @Override
    @Transactional
    public Optional<Agency> getAgencyInfo(com.modules.payment.application.domain.Agency agency, com.modules.payment.application.domain.Client client) {
        AgencyJpaEntity entity = agencyAndClientConvertToEntity(agency, client);
        Optional<AgencyJpaEntity> foundAgencyInfo = agencyRepository.findByAgencyIdAndSiteId(entity.getAgencyId(), entity.getSiteId());
        if (foundAgencyInfo.isEmpty()) {
            throw new UnregisteredAgencyException(EnumResultCode.UnregisteredAgency, agency.getSiteId());
        }
        return foundAgencyInfo.map(this::convertClientModel);
    }

    @Override
    public List<Agency> selectAgencyInfo() {
        List<AgencyJpaEntity> entityList = agencyRepository.findAll();
        List<Agency> agencies = new ArrayList<>();
        for (AgencyJpaEntity entity : entityList) {
            agencies.add(convertClientModel(entity));
        }
        return agencies;
    }


    @Override
    @Transactional
    public void registerAgency(com.modules.payment.application.domain.Agency agency, com.modules.payment.application.domain.Client client, SettleManager settleManager) {
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
    public void updateAgency(com.modules.payment.application.domain.Agency agency, com.modules.payment.application.domain.Client client, String paymentStatus) {
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
    public void updateAgencyExcessCount(com.modules.payment.application.domain.Agency agency, int excessCount) {
        String siteId = agency.getAgencyId() + "-" + agency.getSiteId();
        Optional<AgencyJpaEntity> optionalEntity = agencyRepository.findByAgencyIdAndSiteId(agency.getAgencyId(),siteId);
        if (optionalEntity.isPresent()){
            AgencyJpaEntity entity = optionalEntity.get();
            entity.setExcessCount(Integer.toString(excessCount));
        } else {
            throw new EntityNotFoundException("optionalEntity : " + agency.getAgencyId() + ", " + agency.getSiteId() + "인 엔터티를 찾을 수 없습니다.");
        }
    }


    private AgencyJpaEntity agencyAndClientConvertToEntity(com.modules.payment.application.domain.Agency agency, com.modules.payment.application.domain.Client client) {
        AgencyJpaEntity entity = new AgencyJpaEntity();
        String siteId = agency.getAgencyId() + "-" + agency.getSiteId();
        entity.setAgencyId(agency.getAgencyId());
        entity.setSiteId(siteId);
        entity.setRateSel(client.getRateSel());
        entity.setStartDate(client.getStartDate());
        entity.setEndDate(client.getEndDate());
        return entity;
    }

    private AgencyJpaEntity convertToEntity(com.modules.payment.application.domain.Agency agency, com.modules.payment.application.domain.Client client, SettleManager settleManager) {
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


    private Agency convertClientModel(AgencyJpaEntity entity) {
        Agency agency = new Agency();

        agency.setAgencyId(entity.getAgencyId());
        agency.setSiteId(entity.getSiteId().split("-")[1]);
        agency.setSiteName(entity.getSiteName());
        agency.setCompanyName(entity.getCompanyName());
        agency.setBusinessType(entity.getBusinessType());
        agency.setBizNumber(entity.getBizNumber());
        agency.setCeoName(entity.getCeoName());
        agency.setPhoneNumber(entity.getPhoneNumber());
        agency.setAddress(entity.getAddress());
        agency.setCompanySite(entity.getCompanySite());
        agency.setEmail(entity.getEmail());
        agency.setRateSel(entity.getRateSel());
        agency.setScheduledRateSel(entity.getScheduledRateSel());
        agency.setSiteStatus(entity.getSiteStatus());
        agency.setExtensionStatus(entity.getExtensionStatus());
        agency.setStartDate(entity.getStartDate());
        agency.setEndDate(entity.getEndDate());

        agency.setSettleManagerName(entity.getSettleManagerName());
        agency.setSettleManagerPhoneNumber(entity.getSettleManagerPhoneNumber());
        agency.setSettleManagerEmail(entity.getSettleManagerEmail());

        return agency;
    }


}
