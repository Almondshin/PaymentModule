package com.modules.application.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class Utils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String mapToJSONString(Map<String, String> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            System.out.println("mapToJSONString 실패");
            return null;
        }
    }

    public static <T> T jsonStringToObject(String jsonString, Class<T> valueType) {
        try {
            return objectMapper.readValue(jsonString, valueType);
        } catch (JsonProcessingException e) {
            System.out.println("jsonStringToObject 실패");
            return null;
        }
    }
}
