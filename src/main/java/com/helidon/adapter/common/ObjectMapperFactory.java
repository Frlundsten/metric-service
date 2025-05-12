package com.helidon.adapter.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ObjectMapperFactory {
  private ObjectMapperFactory() {}

  public static ObjectMapper create() {
    ObjectMapper objectMapper = new ObjectMapper();
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    objectMapper.registerModule(javaTimeModule);
    // Use string representation of timestamp
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false);
    return objectMapper;
  }
}
