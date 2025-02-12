package tim.field.application.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BooleanToStringConverter implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        return (attribute != null && attribute) ? "TRUE" : "FALSE";
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        return "TRUE".equals(dbData);
    }
}
