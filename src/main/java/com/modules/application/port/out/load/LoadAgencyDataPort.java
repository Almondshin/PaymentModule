package com.modules.application.port.out.load;

import com.modules.adapter.in.models.ClientDataContainer;
import com.modules.application.domain.Agency;
import com.modules.application.domain.Client;

import java.util.List;
import java.util.Optional;

public interface LoadAgencyDataPort {
    Optional<ClientDataContainer> getAgencyInfo(Agency agency, Client client);

    List<ClientDataContainer> selectAgencyInfo();
}

