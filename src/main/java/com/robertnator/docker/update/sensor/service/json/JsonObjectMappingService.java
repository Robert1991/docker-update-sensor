package com.robertnator.docker.update.sensor.service.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Service
public class JsonObjectMappingService {

    @Autowired
    private ObjectMapper mapper;

    public JsonObjectMappingService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> T mapToClass(String jsonString, Class<T> classOfT, boolean failOnNullProperties)
        throws JsonObjectMappingException {
        try {
            mapper.configure(FAIL_ON_NULL_CREATOR_PROPERTIES, failOnNullProperties);
            mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(jsonString, classOfT);
        } catch (JsonProcessingException e) {
            throw new JsonObjectMappingException(jsonString, classOfT, e);
        }
    }
}
