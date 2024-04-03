package com.modules.adapter.out.persistence.adapter;

import com.modules.adapter.out.persistence.entity.AgencyInfoKeyJpaEntity;
import com.modules.adapter.out.persistence.repository.AgencyInfoKeyRepository;
import com.modules.application.domain.AgencyInfoKey;
import com.modules.application.exceptions.enums.EnumResultCode;
import com.modules.application.exceptions.exceptions.UnregisteredAgencyException;
import com.modules.application.port.out.load.LoadEncryptDataPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AgencyInfoKeyAdapter implements LoadEncryptDataPort {

    private final AgencyInfoKeyRepository agencyInfoKeyRepository;

    public AgencyInfoKeyAdapter( AgencyInfoKeyRepository agencyInfoKeyRepository) {
        this.agencyInfoKeyRepository = agencyInfoKeyRepository;
    }


    @Override
    @Transactional
    public Optional<AgencyInfoKey> getAgencyInfoKey(String agencyId) {
        Optional<AgencyInfoKeyJpaEntity> entity = agencyInfoKeyRepository.findByAgencyId(agencyId);
        if (entity.isEmpty()){
            throw new UnregisteredAgencyException(EnumResultCode.UnregisteredAgency,agencyId);
        }
        return entity.map(this::convertToAgencyInfoKeyDomain);
    }
    private AgencyInfoKey convertToAgencyInfoKeyDomain(AgencyInfoKeyJpaEntity entity){
        AgencyInfoKey agencyInfoKey = new AgencyInfoKey();
        agencyInfoKey.setAgencyId(entity.getAgencyId());
        agencyInfoKey.setAgencyName(entity.getAgencyName());
        agencyInfoKey.setAgencyProductType(entity.getAgencyProductType());
        agencyInfoKey.setAgencyUrl(entity.getAgencyUrl());
        agencyInfoKey.setAgencyKey(entity.getAgencyKey());
        agencyInfoKey.setAgencyIv(entity.getAgencyIv());
        agencyInfoKey.setBillingBase(entity.getBillingBase());
        return agencyInfoKey;
    }



}
