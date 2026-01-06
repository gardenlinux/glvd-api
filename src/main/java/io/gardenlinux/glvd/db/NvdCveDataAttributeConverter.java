package io.gardenlinux.glvd.db;

import jakarta.persistence.AttributeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

public class NvdCveDataAttributeConverter implements AttributeConverter<NvdCve.Data, String> {
    private static final JsonMapper objectMapper = new JsonMapper();
    Logger logger = LoggerFactory.getLogger(NvdCveDataAttributeConverter.class);

    @Override
    public String convertToDatabaseColumn(NvdCve.Data attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JacksonException jpe) {
            logger.warn("Cannot convert CVE Data into JSON");
            return null;
        }
    }

    @Override
    public NvdCve.Data convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, NvdCve.Data.class);
        } catch (JacksonException e) {
            logger.warn("Cannot convert JSON into CVE Data {}", dbData);
            return null;
        }
    }
}
