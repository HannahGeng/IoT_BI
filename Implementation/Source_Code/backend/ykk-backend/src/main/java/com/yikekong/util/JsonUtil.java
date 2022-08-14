package com.yikekong.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

public class JsonUtil{
    /**
     * Deserialization
     * @param json
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T getByJson(String json, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        // Ignore properties that exist in json but not in Java objects during deserialization
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // The default date format during serialization is yyyy-MM-dd'T'HH:mm:ss.SSSZ
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // Deserialization"2020-01-22T11:11:11"The string is LocalDateTime configuration of the format
        mapper.registerModule(new JavaTimeModule());

        return mapper.readValue(json, clazz);
    }

    /**
     * Get value from json string based on nodeName
     * @param nodeName
     * @param json
     * @return
     * @throws IOException
     */
    public static String getValueByNodeName(String nodeName, String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);

        return jsonNode.findPath(nodeName).asText();
    }

    /**
     * serialize
     * @param object
     * @return
     * @throws JsonProcessingException
     */
    public static String serialize(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new JavaTimeModule());

        return mapper.writeValueAsString(object);
    }
}
