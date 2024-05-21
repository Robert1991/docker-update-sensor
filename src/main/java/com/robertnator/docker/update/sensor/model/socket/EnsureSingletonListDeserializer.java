package com.robertnator.docker.update.sensor.model.socket;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.List;

import static java.lang.String.join;

public class EnsureSingletonListDeserializer extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser parser, DeserializationContext context)
        throws IOException {
        List<String> deserializedList = context.readValue(parser,
            context.getTypeFactory().constructCollectionType(List.class, String.class));
        if (deserializedList.size() != 1) {
            throw new RequireSingletonArrayException(deserializedList);
        }
        return deserializedList;
    }

    public static class RequireSingletonArrayException extends JsonProcessingException {

        protected RequireSingletonArrayException(List<String> actualArray) {
            super("there must be exactly one element present in the current token: " + join(",", actualArray));
        }
    }
}
