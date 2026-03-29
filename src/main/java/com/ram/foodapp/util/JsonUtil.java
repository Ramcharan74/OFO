package com.ram.foodapp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.format.DateTimeFormatter;

public class JsonUtil {

    public static final ObjectMapper DEFAULT_MAPPER = createMapper("yyyy-MM-dd HH:mm:ss");

    public static final ObjectMapper DATE_ONLY_MAPPER = createMapper("yyyy-MM-dd");

    public static final ObjectMapper CUSTOM_MAPPER = createMapper("dd-MM-yyyy HH:mm");

    private static ObjectMapper createMapper(String pattern) {
        JavaTimeModule module = new JavaTimeModule();

        module.addSerializer(
                java.time.LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern))
        );

        return new ObjectMapper()
                .registerModule(module)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
