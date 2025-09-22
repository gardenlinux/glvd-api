package io.gardenlinux.glvd.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenlinux.glvd.GlvdService;
import jakarta.persistence.AttributeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NvdCveDataAttributeConverter implements AttributeConverter<NvdCve.Data, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    Logger logger = LoggerFactory.getLogger(GlvdService.class);


    @Override
    public String convertToDatabaseColumn(NvdCve.Data attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException jpe) {
            logger.warn("Cannot convert Address into JSON");
            return null;
        }
    }

    @Override
    public NvdCve.Data convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, NvdCve.Data.class);
        } catch (JsonProcessingException e) {
            logger.warn("Cannot convert JSON into Address");
            return null;
        }
    }
}
