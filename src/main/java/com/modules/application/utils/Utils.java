package com.modules.application.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class Utils {

    public String mapToJSONString(Map<String, String> map) {
        try {
            /* libs  필요 */
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            System.out.println("mapToJSONString 실패");
            return null;
        }
    }
}
