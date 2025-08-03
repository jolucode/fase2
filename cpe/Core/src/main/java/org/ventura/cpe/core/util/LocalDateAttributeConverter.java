package org.ventura.cpe.core.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Converter(autoApply = true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, Date> {

    public static final ZoneId ZONE_AMERICA_LIMA = ZoneId.of("America/Lima");

    @Override
    public Date convertToDatabaseColumn(LocalDate locDate) {
        if (locDate == null) {
            return null;
        }
        // Untested, probably something like this
        ZonedDateTime zonedDateTime = locDate.atStartOfDay(ZONE_AMERICA_LIMA);
        LocalDate producerLocalDate = zonedDateTime.toLocalDate();
        return Date.valueOf(producerLocalDate);
    }

    @Override
    public LocalDate convertToEntityAttribute(Date sqlDate) {
        if (sqlDate == null) {
            return null;
        }
        return sqlDate.toLocalDate();
    }
}
