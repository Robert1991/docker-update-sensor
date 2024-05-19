package com.robertnator.docker.update.sensor.dao.json;

public class JsonObjectMappingException extends Exception {

    public JsonObjectMappingException(String jsonString, Class<?> targetClass, Throwable inner) {
        super(String.format("exception occurred when trying to map json string '%s' to class '%s'",
            jsonString, targetClass), inner);
    }
}
