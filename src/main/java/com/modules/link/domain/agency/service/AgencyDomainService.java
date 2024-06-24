package com.modules.link.domain.agency.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modules.link.application.service.exception.NoSuchFieldException;
import com.modules.link.domain.agency.Agency;
import com.modules.link.enums.EnumResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AgencyDomainService {
    private static final ObjectMapper mapper = new ObjectMapper();

    private final Validator validator;

    public static final String STATUS_TYPE = "status";
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
                case CANCEL_TYPE: {
                    map.put("agencyId", agency.getAgencyId().toString());
                    map.put("siteId", agency.getId().toString());
                    return mapper.writeValueAsString(map);
                }
                default:{
                    map.put("siteId", agency.getId().toString());
                    map.put("siteStatus", STATUS_TYPE_NULL);
                }
            }
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void validateAgency(Agency agency) {
        Set<ConstraintViolation<Agency>> violations = validator.validate(agency);
        if (!violations.isEmpty()) {
            String missingFields = violations.stream()
                    .map(ConstraintViolation::getPropertyPath)
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            throw new NoSuchFieldException(EnumResultCode.NoSuchFieldError, missingFields);
        }
    }
}
