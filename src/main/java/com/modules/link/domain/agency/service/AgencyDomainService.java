package com.modules.link.domain.agency.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modules.link.domain.agency.Agency;
import com.modules.link.domain.agency.SiteId;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AgencyDomainService {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final String STATUS_TYPE = "status";
    private static final String REGISTER_TYPE = "reg";
    private static final String CANCEL_TYPE = "cancel";
    private static final String STATUS_TYPE_NULL = "E";

    public String generateTargetData(Agency agency, String statusType) {
        Map<String, String> map = new HashMap<>();
        try {
            switch (statusType) {
                case STATUS_TYPE: {
                    map.put("siteId", agency.getId().toString());
                    map.put("siteStatus", agency.getAgencyStatus());
                    return mapper.writeValueAsString(map);
                }
                case REGISTER_TYPE: {
                    return mapper.writeValueAsString(this);
                }
                case CANCEL_TYPE: {
                    map.put("agencyId", agency.getAgencyId().toString());
                    map.put("siteId", agency.getId().toString());
                    map.put("siteName", agency.getAgencyCompany().getSiteName());
                    return mapper.writeValueAsString(map);
                }
            }
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateNotFoundStatusData(SiteId siteId) {
        Map<String, String> map = new HashMap<>();
        try {
            map.put("siteId", siteId.toString());
            map.put("siteStatus", STATUS_TYPE_NULL);
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
