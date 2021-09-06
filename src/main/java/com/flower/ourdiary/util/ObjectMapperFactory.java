package com.flower.ourdiary.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.flower.ourdiary.common.Constant;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class ObjectMapperFactory {
    public static ObjectMapper create() {
        return new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setTimeZone(TimeZone.getTimeZone(Constant.DEFAULT_TIME_ZONE))
                .setDateFormat(new SimpleDateFormat(Constant.DEFAULT_DATE_FORMAT));
    }

    public static ObjectMapper createWithDateFormat() {
        return create()
                .setTimeZone(TimeZone.getTimeZone(Constant.DEFAULT_TIME_ZONE))
                .setDateFormat(new SimpleDateFormat(Constant.DEFAULT_DATE_FORMAT));
    }
}
