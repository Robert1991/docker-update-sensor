package com.robertnator.docker.update.sensor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Service
public class JsonObjectMappingService {

    @Autowired
    private ObjectMapper mapper;

    public <T> T mapToClass(String jsonString, Class<T> classOfT) throws JsonProcessingException {
        mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonString, classOfT);
    }
}
