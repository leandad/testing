package team08.apirest.models;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class FormattedLongConverter implements AttributeConverter<Long, String> {

    @Override
    public String convertToDatabaseColumn(Long value) {
        return value == null ? null : value.toString();
    }

    @Override
    public Long convertToEntityAttribute(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Long.valueOf(value.trim().replace(".", "").replace(",", ""));
    }
}
