package com.example.quartz.utils;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.quartz.JobKey;

import java.io.IOException;


public class JobKeyDeserializer extends JsonDeserializer<JobKey> {

    @Override
    public JobKey deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);

        String name = node.get("name").asText();
        String group = node.get("group").asText();
        return new JobKey(name, group);
    }
}

