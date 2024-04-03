package com.modules.adapter.out.persistence.adapter;

import com.modules.adapter.in.models.ClientDataContainer;
import com.modules.adapter.out.persistence.entity.AgencyJpaEntity;
import com.modules.adapter.out.persistence.entity.SiteInfoJpaEntity;
import com.modules.adapter.out.persistence.repository.AgencyRepository;
import com.modules.adapter.out.persistence.repository.SiteInfoRepository;
import com.modules.application.enums.EnumExtensionStatus;
import com.modules.application.enums.EnumPaymentStatus;
import com.modules.application.enums.EnumSiteStatus;
import com.modules.application.domain.Agency;
import com.modules.application.domain.Client;
import com.modules.application.domain.SettleManager;
import com.modules.application.service.NotiService;
import com.modules.application.exceptions.enums.EnumResultCode;
import com.modules.application.exceptions.exceptions.DuplicateMemberException;
import com.modules.application.exceptions.exceptions.UnregisteredAgencyException;
import com.modules.application.port.out.load.LoadAgencyDataPort;
import com.modules.application.port.out.save.SaveAgencyDataPort;
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
    public Optional<ClientDataContainer> getAgencyInfo(Agency agency, Client client) {
        AgencyJpaEntity entity = agencyAndClientConvertToEntity(agency, client);
        Optional<AgencyJpaEntity> foundAgencyInfo = agencyRepository.findByAgencyIdAndSiteId(entity.getAgencyId(), entity.getSiteId());
        if (foundAgencyInfo.isEmpty()) {
            throw new UnregisteredAgencyException(EnumResultCode.UnregisteredAgency, agency.getSiteId());
        }
        return foundAgencyInfo.map(this::convertClientModel);
    }

    @Override
    public List<ClientDataContainer> selectAgencyInfo() {
        List<AgencyJpaEntity> entityList = agencyRepository.findAll();
        List<ClientDataContainer> clientDataContainers = new ArrayList<>();
        for (AgencyJpaEntity entity : entityList) {
            clientDataContainers.add(convertClientModel(entity));
        }
        return clientDataContainers;
    }


    @Override
    @Transactional
    public void registerAgency(Agency agency, Client client, SettleManager settleManager) {
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
    public void updateAgency(Agency agency, Client client, String paymentStatus) {
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


    private AgencyJpaEntity agencyAndClientConvertToEntity(Agency agency, Client client) {
        AgencyJpaEntity entity = new AgencyJpaEntity();
        String siteId = agency.getAgencyId() + "-" + agency.getSiteId();
        entity.setAgencyId(agency.getAgencyId());
        entity.setSiteId(siteId);
        entity.setRateSel(client.getRateSel());
        entity.setStartDate(client.getStartDate());
        entity.setEndDate(client.getEndDate());
        return entity;
    }

    private AgencyJpaEntity convertToEntity(Agency agency, Client client, SettleManager settleManager) {
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


    private ClientDataContainer convertClientModel(AgencyJpaEntity entity) {
        ClientDataContainer clientDataContainer = new ClientDataContainer();

        clientDataContainer.setAgencyId(entity.getAgencyId());
        clientDataContainer.setSiteId(entity.getSiteId().split("-")[1]);
        clientDataContainer.setSiteName(entity.getSiteName());
        clientDataContainer.setCompanyName(entity.getCompanyName());
        clientDataContainer.setBusinessType(entity.getBusinessType());
        clientDataContainer.setBizNumber(entity.getBizNumber());
        clientDataContainer.setCeoName(entity.getCeoName());
        clientDataContainer.setPhoneNumber(entity.getPhoneNumber());
        clientDataContainer.setAddress(entity.getAddress());
        clientDataContainer.setCompanySite(entity.getCompanySite());
        clientDataContainer.setEmail(entity.getEmail());
        clientDataContainer.setRateSel(entity.getRateSel());
        clientDataContainer.setScheduledRateSel(entity.getScheduledRateSel());
        clientDataContainer.setSiteStatus(entity.getSiteStatus());
        clientDataContainer.setExtensionStatus(entity.getExtensionStatus());
        clientDataContainer.setStartDate(entity.getStartDate());
        clientDataContainer.setEndDate(entity.getEndDate());

        clientDataContainer.setSettleManagerName(entity.getSettleManagerName());
        clientDataContainer.setSettleManagerPhoneNumber(entity.getSettleManagerPhoneNumber());
        clientDataContainer.setSettleManagerEmail(entity.getSettleManagerEmail());

        return clientDataContainer;
    }


}
