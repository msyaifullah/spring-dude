package com.yyggee.eggs.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Conversion {
    /**
     * Help convert json string to json node
     *
     * @param jsonString string
     * @return JsonNode
     */
    public static JsonNode strToObj(String jsonString) {
        Logger logger = LoggerFactory.getLogger(Conversion.class);
        try {
            return new ObjectMapper().readTree(jsonString);
        } catch (Exception ex) {
            logger.debug("There is error while parsing string to object", ex);
            return null;
        }
    }

    /**
     * Help convert object json to string
     *
     * @param meta object
     * @return string
     */
    public static String objToStr(Object meta) {
        Logger logger = LoggerFactory.getLogger(Conversion.class);
        try {
            return new ObjectMapper()
//                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(meta);
        } catch (JsonProcessingException ex) {
            logger.error("There is error while parsing string to object", ex);
            return null;
        }
    }
}
